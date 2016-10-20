/*
 * Copyright (c) 2016 Lucko (Luck) <luck@lucko.me>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.commands.track;

import me.lucko.luckperms.common.LuckPermsPlugin;
import me.lucko.luckperms.common.commands.CommandResult;
import me.lucko.luckperms.common.commands.Sender;
import me.lucko.luckperms.common.commands.SingleMainCommand;
import me.lucko.luckperms.common.constants.Message;
import me.lucko.luckperms.common.constants.Permission;
import me.lucko.luckperms.common.data.LogEntry;
import me.lucko.luckperms.common.utils.ArgumentChecker;

import java.util.List;

public class CreateTrack extends SingleMainCommand {
    public CreateTrack() {
        super("CreateTrack", "/%s createtrack <track>", 1, Permission.CREATE_TRACK);
    }

    @Override
    protected CommandResult execute(LuckPermsPlugin plugin, Sender sender, List<String> args, String label) {
        if (args.size() == 0) {
            sendUsage(sender, label);
            return CommandResult.INVALID_ARGS;
        }

        String trackName = args.get(0).toLowerCase();
        if (ArgumentChecker.checkName(trackName)) {
            Message.TRACK_INVALID_ENTRY.send(sender);
            return CommandResult.INVALID_ARGS;
        }

        if (plugin.getDatastore().loadTrack(trackName).getOrDefault(false)) {
            Message.TRACK_ALREADY_EXISTS.send(sender);
            return CommandResult.INVALID_ARGS;
        }

        if (!plugin.getDatastore().createAndLoadTrack(trackName).getOrDefault(false)) {
            Message.CREATE_TRACK_ERROR.send(sender);
            return CommandResult.FAILURE;
        }

        Message.CREATE_SUCCESS.send(sender, trackName);
        LogEntry.build().actor(sender).actedName(trackName).type('T').action("create").build().submit(plugin, sender);
        plugin.runUpdateTask();
        return CommandResult.SUCCESS;
    }
}
