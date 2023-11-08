package com.branchx.agent.helper.api

import java.io.IOException

object Exceptions {
     class ApiException(message: String) : IOException(message)
     class NoInternetException(message: String) : IOException(message)
     class UnAuthorizedException(message : String) : IOException(message)
     class InternalServerError(message : String) : IOException(message)
     class GeneralException(message : String = "Something went wrong! client side") : IOException(message)
     class DoubleTransactionException(message : String) : IOException(message)
}
