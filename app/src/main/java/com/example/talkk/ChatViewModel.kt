package com.example.talkk

import android.content.ContentValues
import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()
    private val userCollection = Firebase.firestore.collection(USERS_COLLECTIONS)
    var userDataListener: ListenerRegistration? = null
    var chatListener: ListenerRegistration? = null
    var chats by mutableStateOf<List<ChatData>>(emptyList())
    var tp by mutableStateOf(ChatData())
    var tpListener: ListenerRegistration? = null
    var reply by mutableStateOf("")
    private val firestore = FirebaseFirestore.getInstance()
    var msgListener: ListenerRegistration? = null
    var messages by mutableStateOf<List<Message>>(listOf())
    var storyListener: ListenerRegistration? = null
    var stories by mutableStateOf<List<Story>>(emptyList())
    fun resetState() {

    }

    fun onSignInResult(signInResult: SignInResult) {
        _state.update {
            it.copy(
                isSignedIn = signInResult.data != null, signInError = signInResult.errorMessage
            )
        }
    }

    fun addUserDataToFireStore(userData: UserData) {
        val userDataMap = mapOf(
            "userId" to userData.userId,
            "username" to userData.userName,
            "ppurl" to userData.ppUrl,
            "email" to userData.userEmail
        )

        val userDocuments = userCollection.document(userData.userId)
        userDocuments.get().addOnSuccessListener {
            if (it.exists()) userDocuments.update(userDataMap).addOnSuccessListener {
                Log.d(ContentValues.TAG, "User data updated to firestore")
            }.addOnFailureListener {
                Log.d(ContentValues.TAG, "User data not updated to firestore")
            }
            else userDocuments.set(userData).addOnSuccessListener {
                Log.d(ContentValues.TAG, "User data added to firestore")
            }.addOnFailureListener {
                Log.d(ContentValues.TAG, "User data not added to firestore")
            }
        }
    }

    fun getUserDataFromFireStore(userId: String) {
        userDataListener = userCollection.document(userId).addSnapshotListener { value, error ->
            if (value != null) {
                _state.update {
                    it.copy(userData = value.toObject(UserData::class.java))
                }
            }
        }
    }

    fun hideDialog() {
        _state.update {
            it.copy(showDialog = false)
        }
    }

    fun showDialog() {
        _state.update {
            it.copy(showDialog = true)
        }
    }

    fun setSrEmail(email: String) {
        _state.update { it.copy(srEmail = email) }
    }

    fun addChat(srEmail: String) {
        Firebase.firestore.collection(CHATS_COLLECTIONS).where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("user1.email", srEmail),
                    Filter.equalTo("user2.email", state.value.userData?.userEmail)
                ), Filter.and(
                    Filter.equalTo("user1.email", state.value.userData?.userEmail),
                    Filter.equalTo("user2.email", srEmail)
                )
            )
        ).get().addOnSuccessListener {
            if (it.isEmpty) {
                userCollection.whereEqualTo("email", srEmail).get().addOnSuccessListener {
                    if (it.isEmpty) println("Failed")
                    else {
                        val chatPartner = it.toObjects(UserData::class.java).firstOrNull()
                        val id = Firebase.firestore.collection(CHATS_COLLECTIONS).document().id
                        val chat = ChatData(
                            chatId = id,
                            last = Message(senderId = "", content = "", time = null),
                            user1 = ChatUserData(
                                userId = state.value.userData?.userId.toString(),
                                typing = false,
                                bio = state.value.userData?.bio.toString(),
                                username = state.value.userData?.userName.toString(),
                                ppUrl = state.value.userData?.ppUrl.toString(),
                                email = state.value.userData?.userEmail.toString(),
                            ),
                            user2 = ChatUserData(
                                userId = chatPartner?.userId.toString(),
                                typing = false,
                                bio = chatPartner?.bio.toString(),
                                username = chatPartner?.userName.toString(),
                                ppUrl = chatPartner?.ppUrl.toString(),
                                email = chatPartner?.userEmail.toString(),
                            )
                        )
                        Firebase.firestore.collection(CHATS_COLLECTIONS).document(id).set(chat)
                    }
                }
            }
        }
    }

    fun showChats(userId: String) {
        chatListener = Firebase.firestore.collection(CHATS_COLLECTIONS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userId), Filter.equalTo("user2.userId", userId)
            )
        ).addSnapshotListener { value, error ->
            if (value != null) {
                chats = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }.sortedBy {
                    it.last?.time
                }.reversed()
            }
        }
    }

    fun getTp(chatId: String) {
        tpListener?.remove()
        tpListener = Firebase.firestore.collection(CHATS_COLLECTIONS).document(chatId)
            .addSnapshotListener { value, error ->
                if (value != null) tp = value.toObject(ChatData::class.java)!!
            }
    }

    fun setChatUser(user: ChatUserData, id: String) {
        _state.update {
            it.copy(
                User2 = user, chatId = id
            )
        }
    }

    fun sendReply(
        chatId: String,
        replyMassage: Message = Message(),
        msg: String,
        senderId: String = state.value.userData?.userId.toString()
    ) {
        val id = Firebase.firestore.collection(CHATS_COLLECTIONS).document()
            .collection(MESSAGE_COLLECTIONS).document().id

        val time = Calendar.getInstance().time

        val message = Message(
            messageId = id,
            repliedMessage = replyMassage,
            senderId = senderId,
            time = Timestamp(date = time),
            content = msg,
        )

        Firebase.firestore.collection(CHATS_COLLECTIONS).document(chatId).collection(
            MESSAGE_COLLECTIONS
        ).document(id).set(message)

        firestore.collection(CHATS_COLLECTIONS).document(chatId).update("last", message)


    }

    fun popMessages(chatId: String) {
        msgListener?.remove()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (chatId != "") {
                    msgListener =
                        firestore.collection(CHATS_COLLECTIONS).document(chatId).collection(
                            MESSAGE_COLLECTIONS
                        ).addSnapshotListener { value, error ->
                            if (value != null) {
                                messages = value.documents.mapNotNull {
                                    it.toObject(Message::class.java)
                                }.sortedBy {
                                    it.time
                                }.reversed()
                            }
                        }
                }
            }
        }
    }

    fun uploadImage(img: Uri, callback: (String) -> Unit) {
        var storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("$IMAGE_COLLECTIONS/${System.currentTimeMillis()}")
        imageRef.putFile(img).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                var url = it.toString()
                callback(url)
            }.addOnFailureListener {
                callback("")
            }
        }.addOnFailureListener {
            callback("")
        }.addOnSuccessListener {

        }
    }

    fun uploadStory(url: String) {
        val image = Image(
            imgUrl = url,
            time = Timestamp(Calendar.getInstance().time)
        )

        val id = firestore.collection(STORIES_COLLECTIONS).document().id
        val story = Story(
            id = id,
            userId = state.value.userData?.userId.toString(),
            userName = state.value.userData?.userName,
            ppUrl = state.value.userData?.ppUrl.toString(),
            images = listOf(image)
        )
        firestore.collection(STORIES_COLLECTIONS).document(id).set(story)
    }

    fun popStory(currUserId: String) {
        viewModelScope.launch {
            val storyCollection = firestore.collection(STORIES_COLLECTIONS)
            val users = arrayListOf(state.value.userData?.userId)
            firestore.collection(CHATS_COLLECTIONS).where(
                Filter.or(
                    Filter.equalTo("user1.userId", currUserId),
                    Filter.equalTo("user2.userId", currUserId)
                )
            ).addSnapshotListener { snp, err ->
                if (snp != null) {
                    snp.toObjects<ChatData>().forEach {
                        val otherUserId =
                            if (it.user1?.userId == currUserId) it.user2?.userId.toString() else it.user1?.userId.toString()
                        users.add(otherUserId)
                    }
                    users.add(currUserId)
                    storyListener = storyCollection.whereIn("userId", users)
                        .addSnapshotListener { storySnapShot, error ->
                            if(storySnapShot != null){
                                stories = storySnapShot.documents.mapNotNull {
                                    it.toObject<Story>()
                                }
                            }
                        }

                }

            }

        }
    }
}