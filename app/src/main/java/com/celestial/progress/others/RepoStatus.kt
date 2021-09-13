package com.celestial.progress.others

import com.celestial.progress.data.model.Counter

interface RepoStatus {
    fun success(obj: Any,msg: String?)
    fun loading(msg: String)
    fun failed(obj: Any, errMsg: String)
}