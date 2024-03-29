package me.aimcventboat.main.commands;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import me.aimcventboat.main.Rdmprct.*;

import static me.aimcventboat.main.Rdmprct.getRandomplace;

public class boat_start implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player){

            Player player = (Player) sender;

            // create a reader
            Reader reader = null;
            try {
                reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs/data.json"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // create parser
            JsonObject parser = null;
            try {
                parser = (JsonObject) Jsoner.deserialize(reader);
            } catch (JsonException ex) {
                ex.printStackTrace();
            }

            List<String> runs = (List<String>) parser.get("runslist");

            try {
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            String nb = args[0];

            if (runs.contains(nb)) {

                try {
                    reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs/" + nb + ".json"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // create parser
                JsonObject run = null;
                try {
                    run = (JsonObject) Jsoner.deserialize(reader);
                } catch (JsonException ex) {
                    ex.printStackTrace();
                }

                JsonObject start_place = (JsonObject) run.get("start_place");

                Integer place = ((List<String>)run.get("players")).size() + 1;

                if ((Boolean) run.get("compet")) {
                    player.sendMessage("§cLa course est en mode compétition.");
                    return false;
                }

                if (place > 5){
                    player.sendMessage("La course est pleine.");
                    return false;
                }

                player.sendMessage(String.valueOf(place));

                List<Double> coord = (List<Double>) start_place.get(String.valueOf(place));

                player.sendMessage(String.valueOf(coord));
                player.sendMessage(String.valueOf(start_place));

                Location loc = player.getLocation();
                loc.setX(Float.valueOf(String.valueOf(coord.get(0)))); loc.setY(Float.valueOf(String.valueOf(coord.get(1)))); loc.setZ(Float.valueOf(String.valueOf(coord.get(2))));


                player.teleport(loc);

                World world = player.getWorld();

                Entity boat = world.spawnEntity(loc, EntityType.BOAT);

                boat.addPassenger(player);

                BufferedWriter writer = null;

                ((List<String>)run.get("players")).add(player.getName());

                try {
                    writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));
                    Jsoner.serialize(parser, writer);
                    writer.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }




        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("boat_start")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();

                try {
                    // create a reader
                    Reader reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));

                    // create parser
                    JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

                    List<String> runs = (List<String>) parser.get("runslist");

                    reader.close();
                    return runs;
                } catch (IOException | JsonException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
