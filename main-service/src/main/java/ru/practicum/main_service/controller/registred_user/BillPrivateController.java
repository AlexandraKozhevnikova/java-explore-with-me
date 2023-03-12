package ru.practicum.main_service.controller.registred_user;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.dto.BillResponse;
import ru.practicum.main_service.mapper.BillMapper;
import ru.practicum.main_service.service.BillService;

@RestController
@RequestMapping("/users/{userId}/bills")
public class BillPrivateController {
    private final BillService billService;

    public BillPrivateController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping
    private BillResponse createBill(@PathVariable("userId") Long userId,
                                    @RequestParam Long eventId){
        return billService.createBill(userId, eventId);
    }
// свагер

    //гет свои и гетр по ИД для собственника или участника или админа
}
