package com.damianmichalak.shopping_list.model.apiModels

import com.google.firebase.database.PropertyName

data class ShoppingList(@PropertyName("date_created")
                        val dateCreated: Long = 0,
                        val name: String = "",
                        private val id: String = "")
