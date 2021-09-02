package com.celestial.progress.others

class Event<out T>(private val content: T) {

    var hasBeenHandled = false
                private set

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandle():T?{
        return if(hasBeenHandled){
            null
        }else{
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content and prevents its use again.
     */
    fun peekContent(): T = content
}