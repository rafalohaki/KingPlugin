package com.rafalohaki.kingplugin

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class KingPlugin : JavaPlugin(), Listener {
    companion object {
        val CROWN = Material.valueOf("GOLDEN_HELMET")
    }

    private val kings = mutableMapOf<Player, Boolean>()

    override fun onEnable() {
        // Enable the plugin
        logger.info("KingPlugin enabled!")
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        // Disable the plugin
        logger.info("KingPlugin disabled!")
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        // Check if the player issued the /king command
        val msg = event.message.trim()
        if (msg.startsWith("/king")) {
            val arguments = msg.split(" ")
            if (arguments.size == 2) {
                val king = Bukkit.getPlayerExact(arguments[1])
                if (king != null) {
                    // Make the player a king
                    kings[king] = true
                    king.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
                    king.equipment?.helmet = ItemStack(CROWN)

                    // Notify everyone
                    server.broadcastMessage(
                        ChatColor.RED.toString() + king.name + " has been crowned the king! Long live the king!" + ChatColor.RESET
                    )
                }
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(event: EntityDeathEvent) {
        val player = event.entity
        val killer = player.killer
        if (player is Player && kings[player] == true && killer != null && killer is Player && player != killer) {
            // Transfer the crown to the killer
            kings[killer] = true
            killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
            killer.equipment?.helmet = ItemStack(CROWN)

            // Notify everyone
            server.broadcastMessage(
                ChatColor.RED.toString() + killer.name + " has killed the king and taken the crown! Long live the new king!" + ChatColor.RESET
            )

            // Remove the item drop
            event.drops.removeIf { it.type == CROWN }
        } else if (player is Player && kings[player] == true) {
            // Notify everyone that the king died
            server.broadcastMessage(
                ChatColor.RED.toString() + player.name + " has died, but there is no one around to claim the crown! The crown is still vacant!" + ChatColor.RESET
            )

            // Remove the item drop
            event.drops.removeIf { it.type == CROWN }

            // Clear the king's status
            kings.remove(player)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        // Remove the king when they quit
        kings.remove(event.player)
    }
}
