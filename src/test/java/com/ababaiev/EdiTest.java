package com.ababaiev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EdiTest {
    @Test
    void testExtractionValidation() throws JsonProcessingException {
        var om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        var result = EdiService.parseSegments("""
                    ЗАГ+0002'ПОЧ'ДЧП+20100910:1048:24'ДОК+ОПЛ'ПЛА+ТОВ \
                    «Кооператор»'БКВ+Чернівецьке відділення КБ «Приватбанк»'МФВ+356032' \
                    РХВ+2600123456789'ОТР+ТОВ «Калинівський ринок»' БКО+ЧФ АКБ \
                    «Укрексімбанк»'МФО+356026'РХО+2600987654321'ОПП+Оплата за товар по \
                    рахунку №23 від 10.09.2010 р.'ВАЛ+грн.'СУМ+10000'КІП'КІН+0016'""");
        var json = om.writeValueAsString(result);
        System.out.println(json);


        var validSegments = EdiService.validateSegments(result);
        System.out.println(om.writeValueAsString(validSegments));
    }

    @Test
    void testExtractionValidation_Failed() throws JsonProcessingException {
        var om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        var result = EdiService.parseSegments("""
                ЗАГ+0002'ПОЧ'ДЧП+20100910:1048:24'ДОК+ОПЛ'ПЛА+ТОВ \
                «Кооператор»'БКВ+Чернівецьке відділення КБ «Приватбанк»'МФВ+356032' \
                РХВ+2600123456789'ОТР+ТОВ «Калинівський ринок»' БКО+ЧФ АКБ \
                «Укрексімбанк»'МФО+356026'РХО+2600987654321'ОПП+Оплата за товар по \
                рахунку №23 від 10.09.2010 р.'ВАЛ+грн.'СУМ+10000'КІП'КІН+0015'""");
        var json = om.writeValueAsString(result);
        System.out.println(json);
        Assertions.assertThrows(RuntimeException.class, () -> EdiService.validateSegments(result));
    }

    @Test
    void testProcess() throws JsonProcessingException {
        var om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        var result = EdiService.process("""
                ЗАГ+0002'ПОЧ'ДЧП+20100910:1048:24'ДОК+ОПЛ'ПЛА+ТОВ \
                «Кооператор»'БКВ+Чернівецьке відділення КБ «Приватбанк»'МФВ+356032' \
                РХВ+2600123456789'ОТР+ТОВ «Калинівський ринок»' БКО+ЧФ АКБ \
                «Укрексімбанк»'МФО+356026'РХО+2600987654321'ОПП+Оплата за товар по \
                рахунку №23 від 10.09.2010 р.'ВАЛ+грн.'СУМ+10000'КІП'КІН+0016'""");
        var json = om.writeValueAsString(result);
        System.out.println(json);
    }
}
