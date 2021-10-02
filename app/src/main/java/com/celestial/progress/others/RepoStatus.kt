package com.celestial.progress.others



interface RepoStatus {
    fun success(obj: Any,msg: String?)
    fun loading(msg: String)
    fun failed(obj: Any, errMsg: String)
}