package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;
import ru.mephi.vikingdemo.service.VikingSpecialService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vikings")
@Tag(name = "Vikings", description = "Операции с викингами")
public class VikingController {

    private final VikingService vikingService;
    private final VikingSpecialService vikingSpecialService;
    private VikingListener vikingListener;

    public VikingController(VikingService vikingService, VikingListener vikingListener,VikingSpecialService vikingSpecialService) {
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;
        this.vikingSpecialService = vikingSpecialService;
    }
    
    @GetMapping
    @Operation(summary = "Получить список созданных викингов", 
            operationId = "getAllVikings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен")
    })
    public List<Viking> getAllVikings() {
        System.out.println("GET /api/vikings called");
        return vikingService.findAll();
    }

    @GetMapping("/test")
    @Operation(summary = "Получить список тестовых викингов", 
            operationId = "getTest")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен")
    })
    public List<String> test() {
        System.out.println("GET /api/vikings/test called");
        return List.of("Ragnar", "Bjorn");
    }
    
    @PostMapping("/post")
    @Operation(summary = "Создать викинга со случайными параметрами", 
            operationId = "post")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг успешно создан")
    })
    public void addViking(){
        System.out.println("POST api/vikings/post called");
        vikingListener.testAdd();
    }

    @GetMapping("/stats/age/greater")
    @Operation(summary = "колво викингов старше ук. возраста")
    public long countByAgeGreaterThan(@RequestParam int age){
        return vikingSpecialService.countByAgeGreaterThan(age);
    }

    @GetMapping("stats/age/less")
    @Operation(summary = "колво викингов моложе ук. возраста")
    public long countByAgeLessThan(@RequestParam int age){
        return vikingSpecialService.countByAgeLessThan(age);
    }

    @GetMapping("stats/age/between")
    @Operation(summary = "колво викингов между ук. возрастами")
    public long countByAgeBetween(@RequestParam int minage,@RequestParam int maxage){
        return vikingSpecialService.countByAgeBetween(minage, maxage);
    }

    @GetMapping("stats/age/outside")
    @Operation(summary = "колво викингов вне ук. возрастов")
    public long countByAgeOutside(@RequestParam int minage,@RequestParam int maxage){
        return vikingSpecialService.countByAgeOutside(minage, maxage);
    }

    @GetMapping("/stats/beard-hair")
    @Operation(summary = "Подсчет по форме бороды и цвету волос")
    public long countByBeardAndHair(@RequestParam BeardStyle beard,
                                    @RequestParam HairColor hair) {
        return vikingSpecialService.countByBeardAndHair(beard, hair);
    }


    @GetMapping("/stats/axes/count")
    @Operation(summary = "Подсчет викингов с определенным количеством топоров")
    public long countByAxesCount(@RequestParam int count) {
        return vikingSpecialService.countByAxesCount(count);
    }

    @GetMapping("/stats/one-axe")
    @Operation(summary = "Викинги с одним топором")
    public long countWithOneAxe() {
        return vikingSpecialService.countByAxesCount(1);
    }

    @GetMapping("/stats/two-axes")
    @Operation(summary = "Викинги с двумя топорами")
    public long countWithTwoAxes() {
        return vikingSpecialService.countByAxesCount(2);
    }

    @GetMapping("/stats/specific-criteria")
    @Operation(summary = "Сложное условие (возраст>30, FORKED, BLOND, 1 топор)")
    public long countBySpecificCriteria() {
        return vikingSpecialService.countBySpecificCriteria();
    }

    @GetMapping("/stats/custom")
    @Operation(summary = "Произвольное условие (пример: возраст > 25, рост > 180, борода LONG)")
    public long customStats() {
        return vikingSpecialService.countVikingsWithCondition(v ->
                v.age() > 25 &&
                        v.heightCm() > 180 &&
                        v.beardStyle() == BeardStyle.LONG &&
                        v.equipment().stream().anyMatch(e -> e.name().contains("AXE"))
        );
    }

    @GetMapping("/random-tall")
    @Operation(summary = "Случайный викинг ростом выше 180 см")
    public Viking getRandomVikingTallerThan180() {
        Viking viking = vikingSpecialService.getRandomVikingTallerThan180();
        if (viking == null) {
            throw new RuntimeException("Нет викингов ростом выше 180 см");
        }
        return viking;
    }

    @GetMapping("/legendary-equipment")
    @Operation(summary = "Все викинги с легендарным снаряжением")
    public List<Viking> getVikingsWithLegendaryEquipment() {
        return vikingSpecialService.getVikingsWithLegendaryEquipment();
    }

    @GetMapping("/redheads")
    @Operation(summary = "Рыжебородые викинги, сортированные по возрасту")
    public List<Viking> getRedheadsSortedByAge(
            @RequestParam(defaultValue = "asc") String order) {

        boolean ascending = "asc".equalsIgnoreCase(order);
        return vikingSpecialService.getRedheadsSortedByAge(ascending);
    }

    @PostMapping("/generate-multiple")
    @Operation(summary = "Массовая генерация викингов")
    public List<Viking> generateMultipleVikings(@RequestParam int count) {
        System.out.println("POST /api/vikings/generate-multiple?count=" + count);
        return vikingSpecialService.generateMultipleVikings(count);
    }
}
