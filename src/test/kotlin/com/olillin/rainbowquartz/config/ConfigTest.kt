package com.olillin.rainbowquartz.config

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.olillin.rainbowquartz.RainbowQuartz
import org.bukkit.NamespacedKey
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ConfigTest {
    private lateinit var server: ServerMock
    private lateinit var plugin: RainbowQuartz
    private val key: NamespacedKey
        get() = NamespacedKey.fromString("foo:${UUID.randomUUID()}")!!

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(RainbowQuartz::class.java)
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun saveConfig() {

    }
}