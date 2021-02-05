package com.worldpay.access.checkout.api.pact

import au.com.dius.pact.consumer.dsl.DslPart
import au.com.dius.pact.core.model.matchingrules.MatchingRuleGroup

/**
 * The behaviour of the Pact JCM consumer Junit library has changed in version 4
 * Colons are allowed as part of identifiers but not escaped in matching rules
 * This is due to a change in behaviour following to this requested change:
 * https://github.com/pact-foundation/pact-jvm/issues/965
 *
 * This is required to escape .xxx:yyy into ['xxx:yyy'] so that the matching rules can
 * be correctly processed during verification on the Provider side
 */
class PactUtils {
    companion object {
        fun escapeColonsInMatchingRules(dslPart: DslPart): DslPart {
            val oldMatchingRules: MutableMap<String, MatchingRuleGroup> =
                dslPart.matchers.matchingRules
            val newMatchingRules: MutableMap<String, MatchingRuleGroup> = HashMap()
            oldMatchingRules.forEach {
                val regex = """[a-zA-Z0-9]+\:[a-zA-Z0-9]+""".toRegex()
                var newKey = it.key
                regex.findAll(it.key).forEach {
                    newKey = newKey.replace(".${it.value}", "['${it.value}']")
                }

                newMatchingRules[newKey] = it.value
            }

            dslPart.matchers.matchingRules = newMatchingRules

            return dslPart
        }
    }
}