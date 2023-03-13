package ru.practicum.main_service.service;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.dto.BillResponse;
import ru.practicum.main_service.dto.event.EvenPaymentsReport;
import ru.practicum.main_service.mapper.BillMapper;
import ru.practicum.main_service.model.BillEntity;
import ru.practicum.main_service.model.BillState;
import ru.practicum.main_service.model.EventEntity;
import ru.practicum.main_service.model.QBillEntity;
import ru.practicum.main_service.model.QEventEntity;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.repository.BillRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.main_service.model.BillState.CREATED;

@Service
public class BillService {
    private final BillMapper billMapper;
    private final UserService userService;
    private final EventService eventService;
    private final RequestService requestService;
    private final CurrencyService currencyService;
    private final BillRepository billRepository;

    public BillService(BillMapper billMapper, UserService userService, EventService eventService,
                       RequestService requestService, CurrencyService currencyService, BillRepository billRepository) {
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
        requestService.checkUserGetConfirmedRequestOnEvent(participant, event);

        Optional<BillEntity> existBill = billRepository.findOne(QBillEntity.billEntity.event.eventId.eq(eventId)
                .and(QBillEntity.billEntity.participant.userId.eq(participant.getUserId()))
                .and(QBillEntity.billEntity.state.in(CREATED, BillState.PAID))
        );

        if (existBill.isPresent()) {
            BillEntity bill = checkBillIsExistAndActualStatusAndGet(existBill.get().getBillId()); //todo
            switch (bill.getState()) {
                case CREATED:
                    return billMapper.responseFromEntity(existBill.get());
                case PAID:
                    throw new IllegalArgumentException("this bill already paid. Bill id = " + existBill.get().getBillId());
            }
        }

        BillEntity billEntity = new BillEntity();
        billEntity.setEvent(event);
        billEntity.setParticipant(participant);
        billEntity.setAmount(event.getAmount());
        billEntity.setCurrency(currencyService.getCurrencyRub());
        billEntity.setState(CREATED);

        return billMapper.responseFromEntity(billRepository.save(billEntity));
    }

    @Transactional
    public BillResponse payBill(Long userId, Long billId) {
        UserEntity participant = userService.checkUserIsExistAndGetById(userId);
        BillEntity bill = checkBillIsExistAndActualStatusAndGet(billId);
        checkUserIsParticipant(participant, bill);

        if (bill.getState() == CREATED) {
            bill.setState(BillState.PAID);
        } else {
            throw new IllegalArgumentException("bill is not CREATED");
        }

        return billMapper.responseFromEntity(bill);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BillEntity checkBillIsExistAndActualStatusAndGet(Long billId) {
        BillEntity bill = billRepository.findById(billId)
                .orElseThrow(() -> new NoSuchElementException("Bill with id=" + billId + " does not exist."));
        if (bill.getState() == CREATED &&
                bill.getCreatedOn().plusMinutes(1L).isBefore(LocalDateTime.now())) {
            bill.setState(BillState.EXPIRED);
        }

        return bill;
    }

    @Transactional(readOnly = true)
    public BillResponse getBillById(Long userId, Long billId) {
        UserEntity user = userService.checkUserIsExistAndGetById(userId);
        BillEntity bill = checkBillIsExistAndActualStatusAndGet(billId);
        checkUserIsParticipant(user, bill);
        return billMapper.responseFromEntity(bill);
    }

    @Transactional
    public List<BillResponse> getBills(Long userId, Optional<BillState> state, Integer pageFrom, Integer pageSize) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(QBillEntity.billEntity.participant.userId.eq(userId));
        state.ifPresent(it -> booleanBuilder.and(QBillEntity.billEntity.state.eq(it)));

        return billRepository.findAll(booleanBuilder, QPageRequest.of(pageFrom, pageSize)).stream()
                .peek(it -> checkBillIsExistAndActualStatusAndGet(it.getBillId()))
                .map(billMapper::responseFromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EvenPaymentsReport> getEventsPayments(Long userId) {
        userService.checkUserIsExistAndGetById(userId);
        List<Tuple> list = billRepository.getEventPaymentsReport(userId);
        return list.stream()
                .map(it -> new EvenPaymentsReport(
                                it.get(QEventEntity.eventEntity.eventId),
                                it.get(1, BigDecimal.class),
                                it.get(2, Long.class),
                                it.get(3, Double.class)
                        )
                )
                .collect(Collectors.toList());
    }

    private void checkThatEvenIsPaid(EventEntity event) {
        if (!event.getPaid() || (event.getPaid() && event.getAmount().compareTo(BigDecimal.valueOf(0)) <= 0)) {
            throw new IllegalArgumentException("Нельзя выставить счет на бесплатное мероприятие, " +
                    "у платного мероприятия должна быть стоимость");
        }
    }

    private void checkUserIsParticipant(UserEntity user, BillEntity bill) {
        if (!bill.getParticipant().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("user has not access to this bill");
        }
    }
}
