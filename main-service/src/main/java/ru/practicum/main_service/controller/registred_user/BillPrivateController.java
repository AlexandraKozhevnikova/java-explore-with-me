package ru.practicum.main_service.controller.registred_user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.BillResponse;
import ru.practicum.main_service.service.BillService;

@RestController
@RequestMapping("/users/{userId}/bills")
@Tag(name = "Private:  Счета на оплату", description = "Счета на оплату могут быть выставлены, оплачены и просмотрены. Счета " +
        "имеют следующие стадии: " + "\n" +
        " - создан (активен для оплаты), " + "\n" +
        " - оплачен, " + "\n" +
        " - протух (не возможен для оплаты - нужно создать новый счет). Через 1 минуту счет протухает")// todo
public class BillPrivateController {
    private final BillService billService;

    public BillPrivateController(BillService billService) {
        this.billService = billService;
    }

    @Operation(
            summary = "Выставление счета на оплату посещения мероприятия для участника",
            description = "Счет на оплату можно выставить только " + "\n" +
                    " - для мероприятия с признаком 'paid = true' и указанной суммой ," + "\n" +
                    " - для мероприятия с указанной  стоимостью мероприятия 'amount'," + "\n" +
                    " - для пользователя с подтвержденным запросом на участие," + "\n" +
                    " - для уникального пользователя - мероприятия, иначе отдается существующий счет."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private BillResponse createBill(
            @PathVariable("userId")
            @Parameter(description = "Идентификатор пользователя", required = true, example = "2")
            Long userId,
            @RequestParam @Parameter(description = "Идентификатор события", required = true, example = "1")
            Long eventId
    ) {
        return billService.createBill(userId, eventId);
    }

    //гет свои и гетр по ИД для собственника или участника или админа
}
