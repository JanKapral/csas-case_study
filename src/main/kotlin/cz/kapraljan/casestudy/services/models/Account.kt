package cz.kapraljan.casestudy.services.models

import java.time.Instant


sealed class Account(
    open val productId: String,
    open val closingDate: Instant
)

data class NationalAccount(
    override val productId: String,
    override val closingDate: Instant,
    val prefix: String,
    val number: String,
    val bankCode: String
) : Account(productId, closingDate)

data class InternationalAccount(
    override val productId: String,
    override val closingDate: Instant,
    val iban: String,
    val currency: Currency
) : Account(productId, closingDate)

enum class Currency {
    CZK, USD, EUR, CHF, GBP
}