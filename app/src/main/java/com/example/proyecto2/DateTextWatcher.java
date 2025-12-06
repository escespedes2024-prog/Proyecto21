package com.example.proyecto2;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputEditText;

public class DateTextWatcher implements TextWatcher {
    private EditText editText;
    private TextInputEditText textInputEditText;
    private boolean isFormatting = false;
    private String format = "yyyy-MM-dd"; // Formato por defecto

    public DateTextWatcher(EditText editText) {
        this.editText = editText;
    }

    public DateTextWatcher(EditText editText, String format) {
        this.editText = editText;
        this.format = format;
    }

    public DateTextWatcher(TextInputEditText textInputEditText) {
        this.textInputEditText = textInputEditText;
    }

    public DateTextWatcher(TextInputEditText textInputEditText, String format) {
        this.textInputEditText = textInputEditText;
        this.format = format;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isFormatting) {
            return;
        }

        String text = s.toString();
        
        // Si el texto ya está vacío, no hacer nada
        if (text.isEmpty()) {
            return;
        }
        
        // Si el texto ya está correctamente formateado, no hacer nada
        if (format.equals("yyyy-MM-dd") && text.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return;
        }
        if (format.equals("dd/MM/yyyy") && text.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            return;
        }

        isFormatting = true;

        // Remover todos los caracteres que no sean números
        String digitsOnly = text.replaceAll("[^0-9]", "");

        String formatted = "";
        
        if (format.equals("yyyy-MM-dd")) {
            // Formato: yyyy-MM-dd
            if (digitsOnly.length() >= 1) {
                formatted = digitsOnly.substring(0, Math.min(4, digitsOnly.length()));
            }
            if (digitsOnly.length() >= 5) {
                formatted += "-" + digitsOnly.substring(4, Math.min(6, digitsOnly.length()));
            }
            if (digitsOnly.length() >= 7) {
                formatted += "-" + digitsOnly.substring(6, Math.min(8, digitsOnly.length()));
            }
        } else if (format.equals("dd/MM/yyyy")) {
            // Formato: dd/MM/yyyy
            if (digitsOnly.length() >= 1) {
                formatted = digitsOnly.substring(0, Math.min(2, digitsOnly.length()));
            }
            if (digitsOnly.length() >= 3) {
                formatted += "/" + digitsOnly.substring(2, Math.min(4, digitsOnly.length()));
            }
            if (digitsOnly.length() >= 5) {
                formatted += "/" + digitsOnly.substring(4, Math.min(8, digitsOnly.length()));
            }
        }

        if (!formatted.equals(text) && !digitsOnly.isEmpty()) {
            int cursorPosition = formatted.length();
            if (editText != null) {
                editText.setText(formatted);
                editText.setSelection(Math.min(cursorPosition, formatted.length()));
            } else if (textInputEditText != null) {
                textInputEditText.setText(formatted);
                textInputEditText.setSelection(Math.min(cursorPosition, formatted.length()));
            }
        }

        isFormatting = false;
    }
}

