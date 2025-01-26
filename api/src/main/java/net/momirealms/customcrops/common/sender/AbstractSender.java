/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
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

package net.momirealms.customcrops.common.sender;

import net.kyori.adventure.text.Component;
import net.momirealms.customcrops.common.plugin.CustomCropsPlugin;
import net.momirealms.customcrops.common.util.Tristate;

import java.util.UUID;

/**
 * Simple implementation of {@link Sender} using a {@link SenderFactory}
 *
 * @param <T> the command sender type
 */
public final class AbstractSender<T> implements Sender {
    private final CustomCropsPlugin plugin;
    private final SenderFactory<?, T> factory;
    private final T sender;

    private final UUID uniqueId;
    private final String name;
    private final boolean isConsole;

    AbstractSender(CustomCropsPlugin plugin, SenderFactory<?, T> factory, T sender) {
        this.plugin = plugin;
        this.factory = factory;
        this.sender = sender;
        this.uniqueId = factory.getUniqueId(this.sender);
        this.name = factory.getName(this.sender);
        this.isConsole = this.factory.isConsole(this.sender);
    }

    @Override
    public CustomCropsPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void sendMessage(Component message) {
        this.factory.sendMessage(this.sender, message);
    }

    @Override
    public void sendMessage(Component message, boolean ignoreEmpty) {
        if (ignoreEmpty && message.equals(Component.empty())) {
            return;
        }
        sendMessage(message);
    }

    @Override
    public Tristate getPermissionValue(String permission) {
        return (isConsole() && this.factory.consoleHasAllPermissions()) ? Tristate.TRUE : this.factory.getPermissionValue(this.sender, permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return (isConsole() && this.factory.consoleHasAllPermissions()) || this.factory.hasPermission(this.sender, permission);
    }

    @Override
    public void performCommand(String commandLine) {
        this.factory.performCommand(this.sender, commandLine);
    }

    @Override
    public boolean isConsole() {
        return this.isConsole;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AbstractSender<?> that)) return false;
        return this.getUniqueId().equals(that.getUniqueId());
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }
}
