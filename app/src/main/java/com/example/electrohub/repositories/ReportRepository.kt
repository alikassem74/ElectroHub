package com.example.electrohub.repositories

import android.util.Log
import com.example.electrohub.models.Reports
import com.example.electrohub.models.Warning
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReportRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private val auth = FirebaseAuth.getInstance()


    private val reportsCollection = firestore.collection("reports")


    // =========================================================
    // SUBMIT REPORT
    // =========================================================

    suspend fun submitReport(
        report: Reports
    ): Result<Unit> {

        return try {

            val currentUserId = getCurrentUserId()

            val reportWithReporter =
                report.copy(
                    reporterId = currentUserId
                )

            reportsCollection
                .add(reportWithReporter)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "REPORT_ERROR",
                "Failed to submit report",
                e
            )

            Result.failure(e)
        }
    }


    // =========================================================
    // GET REPORT BY ID
    // =========================================================

    suspend fun getReportById(
        reportId: String
    ): Result<Reports> {

        return try {

            val document =
                reportsCollection
                    .document(reportId)
                    .get()
                    .await()

            if (!document.exists()) {

                return Result.failure(
                    Exception("Report not found")
                )
            }

            val report =
                document
                    .toObject(
                        Reports::class.java
                    )
                    ?.copy(
                        reportId = document.id
                    )

            if (report == null) {

                Result.failure(
                    Exception("Failed to read report")
                )

            } else {

                Result.success(
                    report
                )
            }

        } catch (e: Exception) {

            Log.e(
                "GET_REPORT",
                "Failed to get report",
                e
            )

            Result.failure(e)
        }
    }
    fun getReports(): Flow<List<Reports>> =
        callbackFlow {

            val listenerRegistration =
                firestore
                    .collection("reports")
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {

                            trySend(emptyList())

                            return@addSnapshotListener
                        }

                        val reports =
                            snapshot
                                ?.documents
                                ?.mapNotNull { document ->

                                    document
                                        .toObject(
                                            Reports::class.java
                                        )
                                        ?.copy(
                                            reportId =
                                                document.id
                                        )
                                }
                                ?: emptyList()

                        trySend(reports)
                    }

            awaitClose {
                listenerRegistration.remove()
            }
        }
    suspend fun updateReportStatus(
        reportId: String,
        status: String
    ): Result<Unit> {

        return try {

            firestore
                .collection("reports")
                .document(reportId)
                .update(
                    "status",
                    status
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "REPORT_STATUS",
                "Failed to update report status",
                e
            )

            Result.failure(e)
        }
    }

    //DisMiss Report
    suspend fun dismissReport(
        reportId: String
    ): Result<Unit> {

        return try {

            firestore
                .collection("reports")
                .document(reportId)
                .update(
                    "status",
                    "dismissed"
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "DISMISS_REPORT",
                "Failed to dismiss report",
                e
            )

            Result.failure(e)
        }
    }
    //Warn User
    suspend fun warnUser(
        userId: String,
        reportId: String,
        productName: String,
        message: String
    ): Result<Unit> {

        return try {

            // ================================================
            // INCREMENT WARNING COUNT
            // ================================================

            firestore
                .collection("users")
                .document(userId)
                .update(
                    "warnings",
                    FieldValue.increment(1)
                )
                .await()


            // ================================================
            // CREATE ADMIN ACTION
            // This triggers the Cloud Function
            // ================================================

            firestore
                .collection("adminActions")
                .add(
                    mapOf(
                        "type" to "warning",
                        "targetUserId" to userId,
                        "reportId" to reportId,
                        "productName" to productName,
                        "message" to message,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()


            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "WARN_USER",
                "Failed to warn user",
                e
            )

            Result.failure(e)
        }
    }

    //Block User
    suspend fun blockUser(
        userId: String
    ): Result<Unit> {

        return try {

            firestore
                .collection("users")
                .document(userId)
                .update(
                    "isBlocked",
                    true
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "BLOCK_USER",
                "Failed to block user",
                e
            )

            Result.failure(e)
        }
    }

    fun getWarnings(
        userId: String
    ): Flow<List<Warning>> = callbackFlow {

        val listenerRegistration =
            firestore
                .collection("adminActions")
                .whereEqualTo(
                    "type",
                    "warning"
                )
                .whereEqualTo(
                    "targetUserId",
                    userId
                )
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        close(error)

                        return@addSnapshotListener
                    }

                    val warnings =
                        snapshot
                            ?.documents
                            ?.mapNotNull { document ->

                                document
                                    .toObject(
                                        Warning::class.java
                                    )
                                    ?.copy(
                                        warningId =
                                            document.id
                                    )
                            }
                            ?.sortedByDescending {
                                it.createdAt
                            }
                            ?: emptyList()

                    trySend(warnings)
                }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun deleteReport(
        reportId: String
    ): Result<Unit> {

        return try {

            reportsCollection
                .document(reportId)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                "DELETE_REPORT",
                "Failed to delete report",
                e
            )

            Result.failure(e)
        }
    }
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in")
    }
}