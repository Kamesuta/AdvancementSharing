package com.kamesuta.advancementsharing;

import org.bukkit.Registry;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class AdvancementSharing extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        // 全員
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        // 解除すべき進捗を抽出
        Stream<Advancement> advancements = Stream.concat(
                // 進捗を達成したものを抽出
                Stream.of(event.getAdvancement()),
                // プレイヤーのいずれかが進捗を達成しているものを抽出
                StreamSupport.stream(Registry.ADVANCEMENT.spliterator(), false)
                        .filter(advancement -> players.stream()
                                .anyMatch(player -> player.getAdvancementProgress(advancement).isDone()))
        );
        // 進捗を解除
        advancements.forEach(advancement -> {
            // 進捗を達成しているプレイヤーの進捗を収集
            for (Player player : players) {
                AdvancementProgress progress = player.getAdvancementProgress(advancement);
                if (!progress.isDone()) {
                    for (String criteria : advancement.getCriteria()) {
                        progress.awardCriteria(criteria);
                    }
                }
            }
        });
    }
}
