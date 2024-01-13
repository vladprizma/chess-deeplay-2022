package io.deeplay.interaction;

/**
 * Возможные типы команд
 */
public enum CommandType {
    //Команды от клиента серверу
    PROTOCOL_VERSION_REQUEST,
    AUTH_REQUEST,
    START_GAME_REQUEST,
    MOVE_REQUEST,
    CANCEL_MOVE_REQUEST,
    DRAW_REQUEST,
    GAME_OVER_REQUEST,
    PING,

    //Команды от сервера клиенту
    PROTOCOL_VERSION_RESPONSE,
    AUTH_RESPONSE,
    START_GAME_RESPONSE,
    MOVE_RESPONSE,
    CANCEL_MOVE_RESPONSE,
    DRAW_RESPONSE,
    GAME_OVER_RESPONSE,
    PONG,
    CONNECTION_CLOSED_RESPONSE,
    GET_ANSWER

}
