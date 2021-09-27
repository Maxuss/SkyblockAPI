package space.maxus.api

data class ItemDataEndpoint(val uuid: String, val item: String, override val code: Int, override val message: String) : Endpoint

data class ExpectedItemData(val data: String)