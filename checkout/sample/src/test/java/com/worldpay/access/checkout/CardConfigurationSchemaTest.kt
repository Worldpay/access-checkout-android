package com.worldpay.access.checkout

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

class CardConfigurationSchemaTest {

    @Test
    fun `given local card configuration file in repo then should be valid against remote card configuration JSON schema`() {
        val url = "https://preprod.access.worldpay.com/access-checkout/cardConfigurationSchema.json"

        val schema = JsonLoader.fromURL(URL(url))
        val cardConfiguration = JsonLoader.fromPath("src/mock/res/raw/card_configuration_file.json")

        val jsonSchemaFactory = JsonSchemaFactory.byDefault()
        val jsonSchema = jsonSchemaFactory.getJsonSchema(schema)

        val validInstance = jsonSchema.validInstance(cardConfiguration)

        assertTrue(validInstance)
    }
}