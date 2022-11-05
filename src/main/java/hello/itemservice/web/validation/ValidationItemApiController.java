package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
@RequiredArgsConstructor
public class ValidationItemApiController {

    private final ItemRepository itemRepository;

//    @ResponseBody
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult){
        log.info("API 컨트롤러 호출");
        if(bindingResult.hasErrors()){
            log.info("검증 오류 발생 errors={}",bindingResult);
            return bindingResult.getAllErrors();
        }
        log.info("성공 로직 실행");

        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        itemRepository.save(item);
        return form;
    }
}
