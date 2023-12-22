package com.mtg.mana;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ManaLoader implements ApplicationRunner {

    @Autowired
    ManaRepository manaRepository;
    @Autowired
    ManaSymbolRepository manaSymbolRepository;

    public void run(final ApplicationArguments args) {
        if (manaRepository.count() != 0) {
            return;
        }
        Mana whiteMana = new Mana(ManaType.COLORED, Color.WHITE, "1");
        manaRepository.save(whiteMana);
        ManaSymbol whiteManaSymbol = new ManaSymbol(new ArrayList<>(List.of(whiteMana)));
        manaSymbolRepository.save(whiteManaSymbol);
        final Mana blueMana = new Mana(ManaType.COLORED, Color.BLUE, "1");
        manaRepository.save(blueMana);
        final ManaSymbol blueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blueMana)));
        manaSymbolRepository.save(blueManaSymbol);
        final Mana blackMana = new Mana(ManaType.COLORED, Color.BLACK, "1");
        manaRepository.save(blackMana);
        final ManaSymbol blackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blackMana)));
        manaSymbolRepository.save(blackManaSymbol);
        final Mana redMana = new Mana(ManaType.COLORED, Color.RED, "1");
        manaRepository.save(redMana);
        final ManaSymbol redManaSymbol = new ManaSymbol(new ArrayList<>(List.of(redMana)));
        manaSymbolRepository.save(redManaSymbol);
        final Mana greenMana = new Mana(ManaType.COLORED, Color.GREEN, "1");
        manaRepository.save(greenMana);
        final ManaSymbol greenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(greenMana)));
        manaSymbolRepository.save(greenManaSymbol);
        final Mana colorlessMana = new Mana(ManaType.COLORLESS, Color.COLORLESS,"1");
        manaRepository.save(colorlessMana);
        final ManaSymbol colorlessManaSymbol = new ManaSymbol(new ArrayList<>(List.of(colorlessMana)));
        manaSymbolRepository.save(colorlessManaSymbol);
        final Mana phyrexianWhiteMana = new Mana(ManaType.PHYREXIAN, Color.WHITE, "1");
        manaRepository.save(phyrexianWhiteMana);
        final ManaSymbol phyrexianWhiteManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana)));
        manaSymbolRepository.save(phyrexianWhiteManaSymbol);
        final Mana phyrexianBlueMana = new Mana(ManaType.PHYREXIAN, Color.BLUE, "1");
        manaRepository.save(phyrexianBlueMana);
        final ManaSymbol phyrexianBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlueMana)));
        manaSymbolRepository.save(phyrexianBlueManaSymbol);
        final Mana phyrexianBlackMana = new Mana(ManaType.PHYREXIAN, Color.BLACK, "1");
        manaRepository.save(phyrexianBlackMana);
        final ManaSymbol phyrexianBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlackMana)));
        manaSymbolRepository.save(phyrexianBlackManaSymbol);
        final Mana phyrexianRedMana = new Mana(ManaType.PHYREXIAN, Color.RED, "1");
        manaRepository.save(phyrexianRedMana);
        final ManaSymbol phyrexianRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianRedMana)));
        manaSymbolRepository.save(phyrexianRedManaSymbol);
        final Mana phyrexianGreenMana = new Mana(ManaType.PHYREXIAN, Color.GREEN, "1");
        manaRepository.save(phyrexianGreenMana);
        final ManaSymbol phyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianGreenMana)));
        manaSymbolRepository.save(phyrexianGreenManaSymbol);
        final Mana genericManaX = new Mana(ManaType.GENERIC, Color.COLORLESS, "X");
        manaRepository.save(genericManaX);
        final ManaSymbol genericManaXSymbol = new ManaSymbol(new ArrayList<>(List.of(genericManaX)));
        manaSymbolRepository.save(genericManaXSymbol);
        final Mana genericMana1 = new Mana(ManaType.GENERIC, Color.COLORLESS, "1");
        manaRepository.save(genericMana1);
        final ManaSymbol genericMana1Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana1)));
        manaSymbolRepository.save(genericMana1Symbol);
        final Mana genericMana2 = new Mana(ManaType.GENERIC, Color.COLORLESS, "2");
        manaRepository.save(genericMana2);
        final ManaSymbol genericMana2Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2)));
        manaSymbolRepository.save(genericMana2Symbol);
        final Mana genericMana3 = new Mana(ManaType.GENERIC, Color.COLORLESS, "3");
        manaRepository.save(genericMana3);
        final ManaSymbol genericMana3Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana3)));
        manaSymbolRepository.save(genericMana3Symbol);
        final Mana genericMana4 = new Mana(ManaType.GENERIC, Color.COLORLESS, "4");
        manaRepository.save(genericMana4);
        final ManaSymbol genericMana4Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana4)));
        manaSymbolRepository.save(genericMana4Symbol);
        final Mana genericMana5 = new Mana(ManaType.GENERIC, Color.COLORLESS, "5");
        manaRepository.save(genericMana5);
        final ManaSymbol genericMana5Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana5)));
        manaSymbolRepository.save(genericMana5Symbol);
        final Mana genericMana6 = new Mana(ManaType.GENERIC, Color.COLORLESS, "6");
        manaRepository.save(genericMana6);
        final ManaSymbol genericMana6Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana6)));
        manaSymbolRepository.save(genericMana6Symbol);
        final Mana genericMana7 = new Mana(ManaType.GENERIC, Color.COLORLESS, "7");
        manaRepository.save(genericMana7);
        final ManaSymbol genericMana7Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana7)));
        manaSymbolRepository.save(genericMana7Symbol);
        final Mana genericMana8 = new Mana(ManaType.GENERIC, Color.COLORLESS, "8");
        manaRepository.save(genericMana8);
        final ManaSymbol genericMana8Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana8)));
        manaSymbolRepository.save(genericMana8Symbol);
        final Mana genericMana9 = new Mana(ManaType.GENERIC, Color.COLORLESS, "9");
        manaRepository.save(genericMana9);
        final ManaSymbol genericMana9Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana9)));
        manaSymbolRepository.save(genericMana9Symbol);
        final Mana genericMana10 = new Mana(ManaType.GENERIC, Color.COLORLESS, "10");
        manaRepository.save(genericMana10);
        final ManaSymbol genericMana10Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana10)));
        manaSymbolRepository.save(genericMana10Symbol);
        final Mana genericMana11 = new Mana(ManaType.GENERIC, Color.COLORLESS, "11");
        manaRepository.save(genericMana11);
        final ManaSymbol genericMana11Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana11)));
        manaSymbolRepository.save(genericMana11Symbol);
        final Mana genericMana12 = new Mana(ManaType.GENERIC, Color.COLORLESS, "12");
        manaRepository.save(genericMana12);
        final ManaSymbol genericMana12Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana12)));
        manaSymbolRepository.save(genericMana12Symbol);
        final Mana genericMana13 = new Mana(ManaType.GENERIC, Color.COLORLESS, "13");
        manaRepository.save(genericMana13);
        final ManaSymbol genericMana13Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana13)));
        manaSymbolRepository.save(genericMana13Symbol);
        final Mana genericMana14 = new Mana(ManaType.GENERIC, Color.COLORLESS, "14");
        manaRepository.save(genericMana14);
        final ManaSymbol genericMana14Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana14)));
        manaSymbolRepository.save(genericMana14Symbol);
        final Mana genericMana15 = new Mana(ManaType.GENERIC, Color.COLORLESS, "15");
        manaRepository.save(genericMana15);
        final ManaSymbol genericMana15Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana15)));
        manaSymbolRepository.save(genericMana15Symbol);
        final Mana genericMana16 = new Mana(ManaType.GENERIC, Color.COLORLESS, "16");
        manaRepository.save(genericMana16);
        final ManaSymbol genericMana16Symbol = new ManaSymbol(new ArrayList<>(List.of(genericMana16)));
        manaSymbolRepository.save(genericMana16Symbol);
        final Mana genericMana1M = new Mana(ManaType.GENERIC, Color.COLORLESS, "1000000");
        manaRepository.save(genericMana1M);
        final ManaSymbol genericMana1MSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana1M)));
        manaSymbolRepository.save(genericMana1MSymbol);
        final ManaSymbol whiteBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(whiteMana, blueMana)));
        manaSymbolRepository.save(whiteBlueManaSymbol);
        final ManaSymbol whiteBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(whiteMana, blackMana)));
        manaSymbolRepository.save(whiteBlackManaSymbol);
        final ManaSymbol blueBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blueMana, blackMana)));
        manaSymbolRepository.save(blueBlackManaSymbol);
        final ManaSymbol blueRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blueMana, redMana)));
        manaSymbolRepository.save(blueRedManaSymbol);
        final ManaSymbol blackRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blackMana, redMana)));
        manaSymbolRepository.save(blackRedManaSymbol);
        final ManaSymbol blackGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blackMana, greenMana)));
        manaSymbolRepository.save(blackGreenManaSymbol);
        final ManaSymbol redGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(redMana, greenMana)));
        manaSymbolRepository.save(redGreenManaSymbol);
        final ManaSymbol whiteRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(whiteMana, redMana)));
        manaSymbolRepository.save(whiteRedManaSymbol);
        final ManaSymbol whiteGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(whiteMana, greenMana)));
        manaSymbolRepository.save(whiteGreenManaSymbol);
        final ManaSymbol blueGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(blueMana, greenMana)));
        manaSymbolRepository.save(blueGreenManaSymbol);
        final ManaSymbol twoWhiteManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, whiteMana)));
        manaSymbolRepository.save(twoWhiteManaSymbol);
        final ManaSymbol twoBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, blueMana)));
        manaSymbolRepository.save(twoBlueManaSymbol);
        final ManaSymbol twoBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, blackMana)));
        manaSymbolRepository.save(twoBlackManaSymbol);
        final ManaSymbol twoRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, redMana)));
        manaSymbolRepository.save(twoRedManaSymbol);
        final ManaSymbol twoGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(genericMana2, greenMana)));
        manaSymbolRepository.save(twoGreenManaSymbol);
        final ManaSymbol whitePhyrexianBlueManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana, phyrexianBlueMana)));
        manaSymbolRepository.save(whitePhyrexianBlueManaSymbol);
        final ManaSymbol whitePhyrexianBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana, phyrexianBlackMana)));
        manaSymbolRepository.save(whitePhyrexianBlackManaSymbol);
        final ManaSymbol bluePhyrexianBlackManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlueMana, phyrexianBlackMana)));
        manaSymbolRepository.save(bluePhyrexianBlackManaSymbol);
        final ManaSymbol bluePhyrexianRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlueMana, phyrexianRedMana)));
        manaSymbolRepository.save(bluePhyrexianRedManaSymbol);
        final ManaSymbol blackPhyrexianRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlackMana, phyrexianRedMana)));
        manaSymbolRepository.save(blackPhyrexianRedManaSymbol);
        final ManaSymbol blackPhyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlackMana, phyrexianGreenMana)));
        manaSymbolRepository.save(blackPhyrexianGreenManaSymbol);
        final ManaSymbol redPhyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianRedMana, phyrexianGreenMana)));
        manaSymbolRepository.save(redPhyrexianGreenManaSymbol);
        final ManaSymbol whitePhyrexianRedManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana, phyrexianRedMana)));
        manaSymbolRepository.save(whitePhyrexianRedManaSymbol);
        final ManaSymbol whitePhyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianWhiteMana, phyrexianGreenMana)));
        manaSymbolRepository.save(whitePhyrexianGreenManaSymbol);
        final ManaSymbol bluePhyrexianGreenManaSymbol = new ManaSymbol(new ArrayList<>(List.of(phyrexianBlueMana, phyrexianGreenMana)));
        manaSymbolRepository.save(bluePhyrexianGreenManaSymbol);
        final Mana snowMana = new Mana(ManaType.SNOW, Color.COLORLESS, "1");
        manaRepository.save(snowMana);
    }
}
