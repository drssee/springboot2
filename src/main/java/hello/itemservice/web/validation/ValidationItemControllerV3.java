package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        //여러 이유로 모델에 빈 객첼르 만들어 전달?
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }
    //@modelattribute -> 각각필드당 @requestparam 실패하면 타입에러 , 성공하면 그때 @validation 작동
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult , RedirectAttributes redirectAttributes, Model model) {

        //@ModelAttribute가 붙은 필드는
        //자동으로 model.addAttribute("item",item)
        // bindingresult가 있어야 modelattribute 값에 오류가 있어도 400에러창으로 바로 안감
        // 있으면 에러들을 잡아서 가지고 있어줌 -> 컨트롤러 로직 사용 가능
        //내가 검증하기전에 잘못된 타입오류로 들어온 값들 같은 경우 스프링이 rejectedValue에 저장 해줌
        //검증 로직
//        if(!StringUtils.hasText(item.getItemName())) {
//            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,null,null,"상품 이름은 필수 입니다."));
//        }
//        if(item.getPrice()==null||item.getPrice()<1000||item.getPrice()>1000000){
//            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,null,null,"가격은 1,000 ~ 1,000,000 까지 허용합니다."));
//        }
//        if(item.getQuantity()==null||item.getQuantity()>=9999){
//            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,null,null,"수량은 최대 9,999 까지 허용합니다."));
//        }
//
//        //특정 필드가 아닌 복합 룰 검증
//        if(item.getPrice()!=null&&item.getQuantity()!=null){
//            int resultPrice = item.getPrice()*item.getQuantity();
//            if(resultPrice<10000){
//                bindingResult.addError(new ObjectError("item",null,null,"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = "+resultPrice));
//            }
//        }
//
//        //검증에 실패하면 다시 입력 폼으로
//        if(bindingResult.hasErrors()){
//            log.info("errors = {}",bindingResult);
//            //bindingresult는 model에 자동으로 넘어감
//            return "validation/v3/addForm";
//        }

        //성공로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

