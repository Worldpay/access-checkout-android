package com.worldpay.access.checkout.sample

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import java.net.URL
import org.junit.Assert.assertTrue
import org.junit.Test

class CardConfigurationSchemaTest {

    @Test
    fun `given local card configuration file in repo then should be valid against remote card configuration JSON schema`() {
        val url = "https://try.access.worldpay.com/access-checkout/cardConfigurationSchema.json"

        val schema = JsonLoader.fromURL(URL(url))
        val cardConfiguration = JsonLoader.fromPath("src/mock/res/raw/card_configuration_file.json")

        val jsonSchemaFactory = JsonSchemaFactory.byDefault()
        val jsonSchema = jsonSchemaFactory.getJsonSchema(schema)

        val validInstance = jsonSchema.validInstance(cardConfiguration)

        assertTrue(validInstance)
    }

    @Test
    fun `card type file in repo should be valid against remote card configuration JSON schema`() {
        val url = "https://try.access.worldpay.com/access-checkout/cardTypesSchema.json"

        val schema = JsonLoader.fromURL(URL(url))
        val cardConfiguration = JsonLoader.fromPath("src/mock/res/raw/card_types.json")

        val jsonSchemaFactory = JsonSchemaFactory.byDefault()
        val jsonSchema = jsonSchemaFactory.getJsonSchema(schema)

        val validInstance = jsonSchema.validInstance(cardConfiguration)

        assertTrue(validInstance)
    }
}
