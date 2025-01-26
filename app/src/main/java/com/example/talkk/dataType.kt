package com.example.talkk

import com.google.firebase.Timestamp

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String = "",
    val userName: String? = "",
    val ppUrl: String? = "",
    val userEmail: String? = "",
    val bio: String = "",
)

data class AppState(
    val isSignedIn: Boolean = false,
    val userData: UserData? = null,
    val signInError: String? = null,
    val srEmail: String = "",
    val showDialog: Boolean = false,
    val chatId: String = "",
    val User2: ChatUserData? = null
)

data class ChatData(
    val chatId: String = "",
    val last: Message? = null,
    val user1: ChatUserData? = null,
    val user2: ChatUserData? = null
)

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val repliedMessage: Message? = null,
    val reaction: List<Reaction> = emptyList(),
    val imageUrl: String = "",
    val fileUrl: String = "",
    val fileName: String = "",
    val fileSize: String = "",
    val videoUrl: String = "",
    val progress: String = "",
    val content: String = "",
    val time: Timestamp? = null,
    val read: Boolean = false,
    val forwarded: Boolean = false,
)

data class Reaction(
    val ppUrl: String = "",
    val userName: String = "",
    val userId: String = "",
    val reaction: String = ""
)

data class ChatUserData(
    val userId: String = "",
    val typing: Boolean = false,
    val bio: String = "",
    val username: String? = "",
    val ppUrl: String = "",
    val email: String = "",
    val status: Boolean = false,
    val unread: Int = 0
)

data class Image(
    val imgUrl: String = "",
    var time: Timestamp? = Timestamp.now()
)

data class Story(
    val id : String = "",
    val userId : String = "",
    val userName : String? = "",
    val ppUrl : String = "",
    val images : List<Image> = emptyList()
)