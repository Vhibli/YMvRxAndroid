package com.yzy.baselibrary.http

/**
 *   @auther : Aleyn
 *   time   : 2019/08/12
 */
class ResponseThrowable : Exception {
    var code: Int
    var errMsg: String

    constructor(error: ERROR, e: Throwable? = null) : super(e) {
        code = error.getKey()
        errMsg = error.getValue()
    }
    constructor(code: Int, msg: String) : super(msg) {
        this.code = code
        this.errMsg = msg
    }
}

