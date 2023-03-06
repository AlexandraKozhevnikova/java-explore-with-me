package ru.practicum.main_service.model.eventStateMachine;

import ru.practicum.main_service.errorHandler.IllegalStateEventException;

public enum EventState {
    /**
     * Событие создано
     */
    CREATED {
        @Override
        public void sentToReview(StateMachine stateMachine) {
            stateMachine.setEventState(EventState.PENDING);
        }

        @Override
        public void publishEvent(StateMachine stateMachine) {
            throw new IllegalStateEventException();
        }

        @Override
        public void rejectEvent(StateMachine stateMachine) {
            throw new IllegalStateEventException();
        }

        @Override
        public void cancelReview(StateMachine stateMachine) {
            throw new IllegalStateEventException();
        }

    },
    /**
     * 1 Ожидание публикации. В статус ожидания публикации событие переходит сразу после создания.
     */
    PENDING {
        @Override
        public void sentToReview(StateMachine stateMachine) {
            // do  nothing
        }

        @Override
        public void publishEvent(StateMachine stateMachine) {
            stateMachine.setEventState(EventState.PUBLISHED);
        }

        @Override
        public void rejectEvent(StateMachine stateMachine) {
            stateMachine.setEventState(EventState.CANCELED);
        }

        @Override
        public void cancelReview(StateMachine stateMachine) {
            stateMachine.setEventState(EventState.CANCELED);
        }
    },
    /**
     * 2 Публикация. В это состояние событие переводит администратор.
     */
    PUBLISHED {
        @Override
        public void sentToReview(StateMachine stateMachine) {
            throw new IllegalStateEventException();
        }

        @Override
        public void publishEvent(StateMachine stateMachine) {
            // do  nothing
        }

        @Override
        public void rejectEvent(StateMachine stateMachine) {
            throw new IllegalStateEventException();
        }

        @Override
        public void cancelReview(StateMachine stateMachine) {
            throw new IllegalStateEventException();
        }
    },
    /**
     * 3 Отмена публикации. В это состояние событие переходит в двух случаях.
     * Первый — если администратор решил, что его нельзя публиковать.
     * Второй — когда инициатор события решил отменить его на этапе ожидания публикации.
     */
    CANCELED {
        @Override
        public void sentToReview(StateMachine stateMachine) {
            stateMachine.setEventState(EventState.PENDING);
        }

        @Override
        public void publishEvent(StateMachine stateMachine) {
            throw new IllegalStateEventException();
        }

        @Override
        public void rejectEvent(StateMachine stateMachine) {
            // do  nothing
        }

        @Override
        public void cancelReview(StateMachine stateMachine) {
            // do  nothing
        }
    };

    public abstract void sentToReview(StateMachine stateMachine);

    public abstract void publishEvent(StateMachine stateMachine);

    public abstract void rejectEvent(StateMachine stateMachine);

    public abstract void cancelReview(StateMachine stateMachine);
}
