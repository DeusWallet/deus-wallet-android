package io.deus.wallet.entities

import androidx.annotation.Keep

@Keep
data class FaqMap(
    val section: HashMap<String, String>,
    val items: List<HashMap<String, Faq>>
)

@Keep
data class Faq(
    val title: String,
    val markdown: String
)

@Keep
data class FaqSection(
    val section: String,
    val faqItems: List<Faq>)