package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import java.util.List;

@RestController
@RequestMapping("/api/vikings")
@Tag(name = "Vikings", description = "Операции с викингами")
public class VikingController {

    private final VikingService vikingService;
    private final VikingListener vikingListener;

    public VikingController(VikingService vikingService, VikingListener vikingListener) {
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;
    }
    
    @GetMapping
    @Operation(summary = "Получить список созданных викингов")
    public List<Viking> getAllVikings() {
        System.out.println("GET /api/vikings called");
        return vikingService.findAll();
    }

    @GetMapping("/test")
    @Operation(summary = "Получить список тестовых викингов")
    public List<String> test() {
        System.out.println("GET /api/vikings/test called");
        return List.of("Ragnar", "Bjorn");
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать нового викинга")
    public Viking addViking(@RequestBody Viking viking) {
        System.out.println("POST /api/vikings called with: " + viking);
        
        Viking saved = vikingService.save(viking);
        
        vikingListener.addViking(saved);
        
        return saved;
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Удалить викинга по имени")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Викинг успешно удален"),
        @ApiResponse(responseCode = "404", description = "Викинг с таким именем не найден")
    })
    public ResponseEntity<String> deleteVikingByName(@PathVariable String name) {
        System.out.println("DELETE /api/vikings/" + name + " called");
        
        boolean deleted = vikingService.deleteByName(name);
        
        if (deleted) {
            vikingListener.deleteViking(name);
            return ResponseEntity.ok("Viking with name '" + name + "' was deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Viking with name '" + name + "' not found");
        }
    }

    @PutMapping("/{name}")
    @Operation(summary = "Полностью обновить викинга")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Викинг успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Викинг не найден")
    })
    public ResponseEntity<Viking> updateViking(
            @PathVariable String name, 
            @RequestBody Viking updatedViking) {
        
        System.out.println("PUT /api/vikings/" + name + " called");
        
        boolean updated = vikingService.update(name, updatedViking);
        
        if (updated) {
            vikingListener.updateViking(name, updatedViking);
            return ResponseEntity.ok(updatedViking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/post")
    public void addVikingLegacy() {
        vikingListener.testAdd();
    }
}