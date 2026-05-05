package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import ru.mephi.vikingdemo.repository.VikingRepository;
import ru.mephi.vikingdemo.repository.VikingStorage;

@Service
public class VikingService {

    private final VikingFactory vikingFactory;
    private final VikingStorage vikingStorage;
    private final VikingRepository vikingRepository;

    @Autowired
    public VikingService(
            VikingFactory vikingFactory,
            VikingStorage vikingStorage,
            VikingRepository vikingRepository
    ) {
        this.vikingFactory = vikingFactory;
        this.vikingStorage = vikingStorage;
        this.vikingRepository = vikingRepository;
    }

    public List<Viking> findAll() {
        return vikingStorage.findAll();
    }

    public Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        return vikingStorage.save(viking);
    }

    public void deleteById(int id) {
        vikingStorage.deleteById(id);
    }



    public long countVikingsWithCondition(Predicate<Viking> condition) {
        return vikingStorage.findAll().stream()
                .filter(condition)
                .count();
    }

    public long countByAgeGreaterThan(int age) {
        return countVikingsWithCondition(v -> v.age() > age);  // v.age(), не v.getAge()
    }

    public long countByAgeLessThan(int age) {
        return countVikingsWithCondition(v -> v.age() < age);
    }

    public long countByAgeBetween(int minAge, int maxAge) {
        return countVikingsWithCondition(v -> v.age() >= minAge && v.age() <= maxAge);
    }

    public long countByAgeOutside(int minAge, int maxAge) {
        return countVikingsWithCondition(v -> v.age() < minAge || v.age() > maxAge);
    }

    public long countByBeardAndHair(BeardStyle beard, HairColor hair) {
        return countVikingsWithCondition(v ->
                v.beardStyle() == beard && v.hairColor() == hair
        );
    }

    public long countByAxesCount(int axesCount) {
        return countVikingsWithCondition(v ->
                v.equipment().stream()
                        .filter(item -> "AXE".equals(item.name()))
                        .count() == axesCount
        );
    }

    @SafeVarargs
    public final long countWithComplexCondition(Predicate<Viking>... conditions) {
        return vikingStorage.findAll().stream()
                .filter(v -> Arrays.stream(conditions).allMatch(cond -> cond.test(v)))
                .count();
    }

    public long countBySpecificCriteria() {
        return countWithComplexCondition(
                v -> v.age() > 30,
                v -> v.beardStyle() == BeardStyle.FORKED,
                v -> v.hairColor() == HairColor.Blond,
                v -> v.equipment().stream().filter(e -> "AXE".equals(e.name())).count() == 1
        );
    }

    public Viking getRandomVikingTallerThan180() {
        List<Viking> tallVikings = vikingStorage.findAll().stream()
                .filter(v -> v.heightCm() > 180)
                .toList();

        if (tallVikings.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return tallVikings.get(random.nextInt(tallVikings.size()));
    }

    public List<Viking> getVikingsWithLegendaryEquipment() {
        return vikingStorage.findAll().stream()
                .filter(v -> v.equipment().stream()
                        .anyMatch(e -> "Legendary".equals(e.quality())))
                .toList();
    }

    public List<Viking> getRedheadsSortedByAge() {
        return vikingStorage.findAll().stream()
                .filter(v -> v.hairColor() == HairColor.Red)  // или "Red"
                .sorted(Comparator.comparingInt(Viking::age))
                .toList();
    }


    public List<Viking> getRedheadsSortedByAge(boolean ascending) {
        Stream<Viking> stream = vikingStorage.findAll().stream()
                .filter(v -> v.hairColor() == HairColor.Red);

        if (ascending) {
            stream = stream.sorted(Comparator.comparingInt(Viking::age));
        } else {
            stream = stream.sorted(Comparator.comparingInt(Viking::age).reversed());
        }

        return stream.toList();
    }

    public List<Viking> generateMultipleVikings(int count) {
        List<Viking> generated = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            Viking viking = vikingFactory.createRandomViking();
            vikingStorage.save(viking);
            generated.add(viking);
            System.out.println("Создан викинг #" + (i + 1) + ": " + viking.name());
        });

        return generated;
    }
}