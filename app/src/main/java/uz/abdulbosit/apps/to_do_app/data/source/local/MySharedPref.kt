package ru.ifr0z.notify.data.source.local

interface MySharedPref {
    var bookName: String?
    var savedPage: Int
    var totalPage: Int
    var isFirst: Boolean
    var bookUrl: String
    var name:String
    var imageUri:String


    fun savedPageByBookName(bookName: String, page: Int)
    fun getSavedPageByBookName(bookName: String): Int
    fun isRegistered(b: Boolean)


    fun isRegistered(): Boolean
}