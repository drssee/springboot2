package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        //여러 이유로 모델에 빈 객첼르 만들어 전달?
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }


    //**************에러코드 프러퍼티****************
    //스프링기준 메시지리졸버빈을 생성 후 프러퍼티스에 지정한 경로의 프러퍼티를 메시지 프러피티로 사용 가능
    //구체적->범용적, max.item.quantity->max.java.lang.Integer->max

    //*************bindingResult 작동 순서 **************
    //bindingResults는, 디스패처 서블릿이 매핑에 맞는 컨트롤러를 찾아 객체를 생성뒤 메서드를 실행 시킬때
    //파라미터의 커맨드 객체에 입력된 reqparam들을 각각 setter로 주입시
    //이 작업이 성공하면 -> 유효한지 검증(validator작동) , 실패 하면 -> 타입미스매치예외


    //******* 검증 배운 단계순서대로 ********* (오브젝트 에러는 따로 직접 수동 검증이 편함<-메서드 추출)
    //1.검증 오류가 있으면 수동 검증 로직으로 검증 후, addFieldError(),addObjectError()를 이용해 에러로 등록할수 있음(뷰가 인식)
    //2.에러객체(errors,bindingResult)를 이용해 rejectValue()를 이용 에러로 등록할 수 있음(내부적으로 1번 사용)(뷰가 인식)
    //3.validator 사용 (커스텀~제공lib)(전역~범위지정)
    //4.validate-api->hibernate validator 어느테이션 사용

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //**************검증에 실패하면 다시 입력 폼으로(스프링 기준 ,입력폼에 에러메시지를 받는 코드가 있어야 함)***************
//        if (bindingResult.hasErrors()) {
//        log.info("errors={} ", bindingResult);
//        return "validation/v4/addForm";
//    }


    //@modelattribute -> 각각필드당 @requestparam 실패하면 타입에러 , 성공하면 그때 @validation 작동
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult , RedirectAttributes redirectAttributes, Model model) {


        //특정 필드가 아닌 복합 룰 검증
        if (form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //**************검증에 실패하면 다시 입력 폼으로(스프링 기준 ,입력폼에 에러메시지를 받는 코드가 있어야 함)***************
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v4/addForm";
        }


        //성공로직 일때 itemsaveform -> item 생성후 넘겨준다

        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form,
                       BindingResult bindingResult) {
        //특정 필드가 아닌 복합 룰 검증
        if (form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //검증 실패시 자신을 호출했던 겟 메시지 페이지로 다시 이동
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v4/editForm";
        }


        //성공시 item 생성후 넘김

        Item itemParam = new Item();
        itemParam.setItemName(form.getItemName());
        itemParam.setPrice(form.getPrice());
        itemParam.setQuantity(form.getQuantity());

        itemRepository.update(itemId, itemParam);
        return "redirect:/validation/v4/items/{itemId}";
    }


}

