package com.example.demo3.common;
import java.util.List;

public class LlmResponse {
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }
    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
}