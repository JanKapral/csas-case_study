package cz.kapraljan.casestudy.exceptions

sealed class ExternalApiException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class ClientErrorException(clientId: String?, service: String, statusCode: Int, cause: Throwable? = null) :
    ExternalApiException("Client error in $service service: HTTP $statusCode${if (clientId != null) " for client '$clientId'" else ""}", cause)

class ServerErrorException(service: String, statusCode: Int, cause: Throwable? = null) :
    ExternalApiException("Server error in $service service: HTTP $statusCode", cause)
