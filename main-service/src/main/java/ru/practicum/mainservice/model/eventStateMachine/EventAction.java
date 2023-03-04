package ru.practicum.mainservice.model.eventStateMachine;

public enum EventAction {
    /**
     * Инициатор события, отправил событие на модерацию администратору
     */
    SEND_TO_REVIEW,
    /**
     * Админ опубликовал событие
     */
    PUBLISH_EVENT,
    /**
     * Админ отклонил событие
     */
    REJECT_EVENT,
    /**
     * Пользователь отменил событие
     */
    CANCEL_REVIEW
}
