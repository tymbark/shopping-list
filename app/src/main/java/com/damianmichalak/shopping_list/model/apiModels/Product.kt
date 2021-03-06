package com.damianmichalak.shopping_list.model.apiModels

data class Product(val id: String = "",
                   val name: String = "",
                   val dateAdded: Long = -1,
                   val datePurchased: Long = -1) {

    fun purchased(): Product {
        return Product(id, name, dateAdded, System.currentTimeMillis())
    }

    fun withId(key: String): Product {
        return Product(key, name, dateAdded, datePurchased)
    }

}
