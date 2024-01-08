package rinhav4.domain;

import io.micronaut.http.HttpStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

@Converter
public class PessoaStackConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = ";";

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    @Override
    public String convertToDatabaseColumn(List<String> stringList) {

        if (Objects.isNull(stringList)) return "";

        if (stringList.stream().anyMatch(PessoaStackConverter::isNumeric)) {

            throw new IllegalStateException("Não é permitido números no campo stack");

//            throw new RuntimeException("Não é permitido números no campo stack"); // TODO: mudar
        }

        return String.join(SPLIT_CHAR, stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        return string != null ? Arrays.asList(string.split(SPLIT_CHAR)) : emptyList();
    }
}

