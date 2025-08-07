package cz.kapraljan.casestudy.adapters.accounts.models

import cz.kapraljan.casestudy.services.models.Currency
import cz.kapraljan.casestudy.services.models.InternationalAccount
import cz.kapraljan.casestudy.services.models.NationalAccount
import java.time.Instant

data class GetAccountsResponseDto(
    val client: ClientDto,
    val accounts: List<AccountDto>
)

data class ClientDto(
    val id: String,
    val name: String? = null
)

sealed class AccountDto(
    open val product_id: String,
    open val closing_date: Instant,
)

data class NationalAccountDto(
    override val product_id: String,
    override val closing_date: Instant,
    val prefix: String,
    val number: String,
    val bank_code: String,
) : AccountDto(product_id, closing_date)

data class InternationalAccountDto(
    override val product_id: String,
    override val closing_date: Instant,
    val iban: String,
    val currency: CurrencyDto
) : AccountDto(product_id, closing_date)

enum class CurrencyDto {
    CZK, USD, EUR, CHF, GBP
}

fun NationalAccountDto.toDomainModel(): NationalAccount = NationalAccount(
    productId = product_id,
    closingDate = closing_date,
    prefix = prefix,
    number = number,
    bankCode = bank_code
)

fun InternationalAccountDto.toDomainModel(): InternationalAccount = InternationalAccount(
    productId = product_id,
    closingDate = closing_date,
    iban = iban,
    currency = Currency.valueOf(currency.name)
)
