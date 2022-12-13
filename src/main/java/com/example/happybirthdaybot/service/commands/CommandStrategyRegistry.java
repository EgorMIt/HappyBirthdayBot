package com.example.happybirthdaybot.service.commands;

import com.example.happybirthdaybot.common.Command;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Реестр имплементаций сервиса.
 *
 * @author Egor Mitrofanov.
 */
@Component
public class CommandStrategyRegistry {

    /**
     * Мапа всех стратегий обработки команд
     * <p>
     * {@link Command}
     * {@link CommandStrategy}
     */
    private final Map<Command, CommandStrategy> commandStrategyMap;

    public CommandStrategyRegistry(Set<CommandStrategy> commandStrategies) {
        Map<Command, CommandStrategy> commandStrategyMap = new HashMap<>();
        commandStrategies.forEach(command -> commandStrategyMap.put(command.getSupportedCommand(), command));
        this.commandStrategyMap = commandStrategyMap;
    }

    public Optional<CommandStrategy> getHandlerForCommand(Command command) {
        return Optional.ofNullable(commandStrategyMap.get(command));
    }
}
