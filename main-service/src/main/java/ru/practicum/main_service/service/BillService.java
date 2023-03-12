package ru.practicum.main_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.dto.BillResponse;
import ru.practicum.main_service.mapper.BillMapper;
import ru.practicum.main_service.model.BillEntity;
import ru.practicum.main_service.model.BillState;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.QBillEntity;
import ru.practicum.main_service.model.RequestEntity;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.repository.BillRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BillService {

    private final BillMapper billMapper;
    private final UserService userService;
    private final EventService eventService;
    private final RequestService requestService;

    private final CurrencyService currencyService;

    private final BillRepository billRepository;

    public BillService(BillMapper billMapper, UserService userService, EventService eventService, RequestService requestService, CurrencyService currencyService, BillRepository billRepository) {
        this.billMapper = billMapper;
        this.userService = userService;
        this.eventService = eventService;
        this.requestService = requestService;
        this.currencyService = currencyService;
        this.billRepository = billRepository;
    }

    @Transactional
    public BillResponse createBill(Long userId, Long eventId) {
        UserEntity participant = userService.checkUserIsExistAndGetById(userId);
        EventEntity event = eventService.checkEventIsExistAndGet(eventId);
        checkThatEvenIsPaid(event);
        RequestEntity request = requestService.checkUserGetConfirmedRequestOnEvent(participant, event);

        Optional<BillEntity> existBill = billRepository.findOne(QBillEntity.billEntity.event.eventId.eq(eventId)
                .and(QBillEntity.billEntity.participant.userId.eq(participant.getUserId()))
                .and(QBillEntity.billEntity.state.eq(BillState.CREATED))
        );

        if (existBill.isPresent()) {
            return billMapper.responseFromEntity(existBill.get());
        }

        BillEntity billEntity = new BillEntity();
        billEntity.setEvent(event);
        billEntity.setParticipant(participant);
        billEntity.setAmount(BigDecimal.valueOf(1000L)); //todo
        billEntity.setCurrency(currencyService.getCurrencyRub());
        billEntity.setState(BillState.CREATED);


        return billMapper.responseFromEntity(billRepository.save(billEntity));
    }

    private void checkThatEvenIsPaid(EventEntity event) {
        if (!event.getPaid() || (event.getPaid() && event.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0)) {
            throw new IllegalArgumentException("Нельзя выставить счет на бесплатное мероприятие, " +
                    "у платного мероприятия должна быть стоимость");
        }
    }
}
