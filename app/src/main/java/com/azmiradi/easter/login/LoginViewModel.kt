package com.azmiradi.easter.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.azmiradi.easter.BaseViewModel
import com.azmiradi.easter.DataState
import com.azmiradi.easter.MyPreferences
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val myPreferences: MyPreferences) : BaseViewModel() {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var job: Job? = null
    private val _state = mutableStateOf(DataState<Boolean>())
    val state: State<DataState<Boolean>> = _state


    private val _errorUsername = mutableStateOf(false)
    val errorUsername: State<Boolean> = _errorUsername

    private val _errorPassword = mutableStateOf(false)
    val errorPassword: State<Boolean> = _errorPassword


    fun login(username: String, password: String) {
        _state.value = DataState()
        isValidLoginRequest(username, password)?.let {
            _state.value = DataState(isLoading = true)
            checkUserExist(username, password,
                userExit = {
                    saveData(false,username)
                    _state.value = DataState(data = true)
                }, userNotExit = {
                    _state.value =
                        DataState(error = "Error Information ! ")
                }) {
                _state.value = DataState(error = it)

            }
        }
    }

    private fun checkUserExist(
        username: String,
        password: String,
        userNotExit: () -> Unit,
        userExit: (String) -> Unit,
        error: (String) -> Unit
    ) {
        databaseReference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnap: DataSnapshot in snapshot.children) {
                        val user = userSnap.getValue(User::class.java)
                        user?.let {
                            if (it.username?.trim() == username.trim() && it.password?.trim()
                                == password.trim()
                            ) {
                                userExit(userSnap.key ?: "")
                                return
                            }
                        }
                    }
                    userNotExit()
                }

                override fun onCancelled(error: DatabaseError) {
                    error(error.message)
                }

            })
    }

    private fun isValidLoginRequest(email: String, password: String): User? {
        return if (email.isEmpty() || password.isEmpty()) {
            _errorUsername.value = email.isEmpty()
            _errorPassword.value = password.isEmpty()
            null
        } else
            User(username = email, password = password)
    }

    private fun saveData(isAdmin: Boolean, userName: String) {
        myPreferences.isLogin = true
        myPreferences.isAdmin = isAdmin
        myPreferences.userName = userName

    }

    override fun resetState() {
        _state.value = DataState()
        _errorUsername.value = false
        _errorPassword.value = false
        job?.cancel()
    }

    override fun isLoading(): Boolean {
        return _state.value.isLoading
    }

    override fun toastMessage(): String {
        return _state.value.error
    }

    fun loginAdmin(username: String, password: String) {
        if (username.equals("admin", true)
            &&
            password.equals("20232023", true)
        ) {
            saveData(true, username)
            _state.value = DataState(data = true)

        } else {
            _state.value =
                DataState(error = "Error Information ! ")
        }
    }

}