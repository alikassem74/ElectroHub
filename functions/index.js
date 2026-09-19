const { onDocumentCreated } = require("firebase-functions/v2/firestore");

const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");


initializeApp();

const db = getFirestore();
const ADMIN_UID = "WnLBVcVojVfuzmDDKoNYpq8R1qn2";



exports.sendMessageNotification = onDocumentCreated(
    "chats/{chatId}/messages/{messageId}",

    async (event) => {

        const messageData = event.data.data();


        if (!messageData) {
            console.log("No message data");
            return;
        }


        const message = messageData.message;
        const senderId = messageData.senderId;


        const chatId = event.params.chatId;



        // Get chat information
        const chatSnapshot = await db
            .collection("chats")
            .doc(chatId)
            .get();



        if (!chatSnapshot.exists) {
            console.log("Chat not found");
            return;
        }



        const chatData = chatSnapshot.data();



        // Find receiver
        let receiverId;


        if (senderId === chatData.buyerId) {

            receiverId = chatData.sellerId;

        } else {

            receiverId = chatData.buyerId;

        }



        // Get receiver data
        const receiverSnapshot = await db
            .collection("users")
            .doc(receiverId)
            .get();



        if (!receiverSnapshot.exists) {

            console.log("Receiver does not exist");
            return;

        }



        const receiverData = receiverSnapshot.data();


        const fcmToken = receiverData.fcmToken;



        if (!fcmToken) {

            console.log("Receiver has no FCM token");
            return;

        }




        // Get sender name
        const senderSnapshot = await db
            .collection("users")
            .doc(senderId)
            .get();



        let senderName = "Someone";



        if (senderSnapshot.exists) {

            senderName =
                senderSnapshot.data().name || "Someone";

        }





        // Create notification

        const notification = {


            token: fcmToken,


            notification: {

                title: `${senderName} sent you a message`,

                body: message

            },


            data: {

                chatId: chatId,

                senderId: senderId

            }


        };





        try {


            await getMessaging().send(notification);


            console.log(
                "Notification sent successfully"
            );


        } catch (error) {


            console.error(
                "Notification failed:",
                error
            );


        }


    }
);

exports.sendReportNotification = onDocumentCreated(
    "reports/{reportId}",

    async (event) => {

        const reportData = event.data.data();

        if (!reportData) {
            console.log("No report data");
            return;
        }

        const reportId = event.params.reportId;

        // =====================================================
        // GET ADMIN
        // =====================================================

        const adminSnapshot = await db
            .collection("users")
            .doc(ADMIN_UID)
            .get();

        if (!adminSnapshot.exists) {
            console.log("Admin does not exist");
            return;
        }

        const adminData = adminSnapshot.data();

        const fcmToken = adminData.fcmToken;

        if (!fcmToken) {
            console.log("Admin has no FCM token");
            return;
        }

        // =====================================================
        // REPORT INFORMATION
        // =====================================================

        const productName =
            reportData.productName || "Unknown Product";

        const reason =
            reportData.reason || "Unknown reason";

        // =====================================================
        // CREATE NOTIFICATION
        // =====================================================

        const notification = {

            token: fcmToken,

            notification: {

                title: "🚨 New Report",

                body:
                    `${productName} was reported: ${reason}`

            },

            data: {

                type: "report",

                reportId: reportId,

                productId:
                    reportData.productId || ""

            }
        };

        // =====================================================
        // SEND NOTIFICATION
        // =====================================================

        try {

            await getMessaging()
                .send(notification);

            console.log(
                "Report notification sent to admin"
            );

        } catch (error) {

            console.error(
                "Report notification failed:",
                error
            );
        }
    }
);


exports.sendAdminWarningNotification = onDocumentCreated(
    "adminActions/{actionId}",

    async (event) => {

        const actionData = event.data.data();

        if (!actionData) {
            console.log("No admin action data");
            return;
        }


        // Only handle warning actions
        if (actionData.type !== "warning") {
            console.log("Not a warning action");
            return;
        }


        const targetUserId =
            actionData.targetUserId;

        const message =
            actionData.message ||
            "You have received a warning from the administrator.";


        if (!targetUserId) {
            console.log("No target user ID");
            return;
        }


        // =====================================================
        // GET TARGET USER
        // =====================================================

        const userSnapshot = await db
            .collection("users")
            .doc(targetUserId)
            .get();


        if (!userSnapshot.exists) {

            console.log(
                "Target user does not exist"
            );

            return;
        }


        const userData =
            userSnapshot.data();


        const fcmToken =
            userData.fcmToken;


        if (!fcmToken) {

            console.log(
                "Target user has no FCM token"
            );

            return;
        }


        // =====================================================
        // SEND NOTIFICATION
        // =====================================================

        const notification = {

            token: fcmToken,

            notification: {

                title: "⚠️ Warning from ElectroHub",

                body: message

            },

            data: {

                type: "admin_warning",

                reportId:
                    actionData.reportId || "",

                actionId:
                    event.params.actionId

            }

        };


        try {

            await getMessaging()
                .send(notification);


            console.log(
                "Warning notification sent successfully"
            );


        } catch (error) {

            console.error(
                "Warning notification failed:",
                error
            );

        }

    }
);