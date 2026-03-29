package org.convoy.pinyinime;

import android.inputmethodservice.InputMethodService;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConvoyPinyinImeService extends InputMethodService {
    private static final int LIGHT_BG = Color.parseColor("#EAEAEA");
    private static final int LIGHT_PANEL = Color.parseColor("#D8D8D8");
    private static final int LIGHT_TEXT = Color.parseColor("#111111");
    private static final int LIGHT_ACTIVE = Color.parseColor("#B9DFFF");
    private static final int DARK_BG = Color.parseColor("#1B1B1B");
    private static final int DARK_PANEL = Color.parseColor("#2A2A2A");
    private static final int DARK_TEXT = Color.parseColor("#F2F2F2");
    private static final int DARK_ACTIVE = Color.parseColor("#3A6F99");

    private enum InputMode {
        SIMPLIFIED,
        TRADITIONAL,
        ENGLISH
    }

    private static final String[] ROW1 = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"};
    private static final String[] ROW2 = {"a", "s", "d", "f", "g", "h", "j", "k", "l"};
    private static final String[] ROW3 = {"Shift", "z", "x", "c", "v", "b", "n", "m", "⌫"};
    private static final String[] ROW4 = {"符", ",", "Space", ".", "Enter"};
    private static final String[] SYMBOL_ROW1 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    private static final String[] SYMBOL_ROW2 = {"-", "/", ":", ";", "(", ")", "$", "&", "@", "\""};
    private static final String[] SYMBOL_ROW3 = {"。", "，", "？", "！", "：", "；", "（", "）", "、", "⌫"};
    private static final String[] SYMBOL_ROW4 = {"ABC", ".", ",", "Space", "Enter"};

    private final PinyinEngine engine = new PinyinEngine();
    private final StringBuilder composing = new StringBuilder();
    private final List<String> currentCandidates = new ArrayList<>();
    private int candidateOffset = 0;
    private InputMode inputMode = InputMode.SIMPLIFIED;
    private boolean shiftOn = false;
    private boolean symbolsMode = false;

    private TextView composingText;
    private LinearLayout candidateContainer;
    private Button candidatePrev;
    private Button candidateNext;
    private Button modeSimplified;
    private Button modeTraditional;
    private Button modeEnglish;
    private LinearLayout row1;
    private LinearLayout row2;
    private LinearLayout row3;
    private LinearLayout row4;
    private View rootView;

    @Override
    public View onCreateInputView() {
        View root = getLayoutInflater().inflate(R.layout.input_view, null);
        rootView = root;
        composingText = root.findViewById(R.id.composing_text);
        candidateContainer = root.findViewById(R.id.candidate_container);
        candidatePrev = root.findViewById(R.id.candidate_prev);
        candidateNext = root.findViewById(R.id.candidate_next);
        modeSimplified = root.findViewById(R.id.mode_simplified);
        modeTraditional = root.findViewById(R.id.mode_traditional);
        modeEnglish = root.findViewById(R.id.mode_english);
        row1 = root.findViewById(R.id.row1);
        row2 = root.findViewById(R.id.row2);
        row3 = root.findViewById(R.id.row3);
        row4 = root.findViewById(R.id.row4);

        candidatePrev.setOnClickListener(v -> pageCandidates(-5));
        candidateNext.setOnClickListener(v -> pageCandidates(5));
        modeSimplified.setOnClickListener(v -> setInputMode(InputMode.SIMPLIFIED));
        modeTraditional.setOnClickListener(v -> setInputMode(InputMode.TRADITIONAL));
        modeEnglish.setOnClickListener(v -> setInputMode(InputMode.ENGLISH));

        rebuildKeyboard();
        applyThemeColors();
        refreshComposingUi();
        refreshCandidates();
        return root;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        composing.setLength(0);
        candidateOffset = 0;
        applyThemeColors();
        refreshComposingUi();
        refreshCandidates();
    }

    private void rebuildKeyboard() {
        row1.removeAllViews();
        row2.removeAllViews();
        row3.removeAllViews();
        row4.removeAllViews();
        if (symbolsMode) {
            addRow(row1, SYMBOL_ROW1);
            addRow(row2, SYMBOL_ROW2);
            addRow(row3, SYMBOL_ROW3);
            addRow(row4, SYMBOL_ROW4);
        } else {
            addRow(row1, ROW1);
            addRow(row2, ROW2);
            addRow(row3, ROW3);
            addRow(row4, ROW4);
        }
        applyThemeColors();
    }

    private void addRow(LinearLayout row, String[] keys) {
        for (String key : keys) {
            Button button = new Button(this);
            button.setText(labelFor(key));
            if (Build.VERSION.SDK_INT >= 26) {
                button.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }
            button.setSingleLine(false);
            button.setMaxLines(2);
            button.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, widthFor(key));
            lp.setMargins(2, 2, 2, 2);
            button.setLayoutParams(lp);
            button.setOnClickListener(v -> onKeyPressed(key));
            row.addView(button);
        }
    }

    private float widthFor(String key) {
        if ("Space".equals(key)) {
            return 2.0f;
        }
        if ("Shift".equals(key) || "⌫".equals(key) || "符".equals(key) || "ABC".equals(key) || "Enter".equals(key)) {
            return 1.5f;
        }
        return 1f;
    }

    private String labelFor(String key) {
        if ("Shift".equals(key)) {
            return getString(R.string.key_shift);
        }
        if ("Space".equals(key)) {
            return getString(R.string.key_space);
        }
        if ("Enter".equals(key)) {
            return getString(R.string.key_enter);
        }
        if ("⌫".equals(key)) {
            return getString(R.string.key_backspace);
        }
        if ("符".equals(key)) {
            return getString(R.string.key_symbols);
        }
        if ("ABC".equals(key)) {
            return getString(R.string.key_back_to_letters);
        }
        if (key.length() == 1 && Character.isLetter(key.charAt(0))) {
            return shiftOn ? key.toUpperCase() : key;
        }
        return key;
    }

    private void onKeyPressed(String key) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return;
        }

        switch (key) {
            case "Shift":
                shiftOn = !shiftOn;
                rebuildKeyboard();
                return;
            case "⌫":
                handleBackspace(ic);
                return;
            case "Space":
                handleSpace(ic);
                return;
            case "Enter":
                commitComposing(ic);
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                return;
            case "符":
                symbolsMode = true;
                rebuildKeyboard();
                return;
            case "ABC":
                symbolsMode = false;
                rebuildKeyboard();
                return;
            default:
                handleTextKey(ic, key);
        }
    }

    private void setInputMode(InputMode nextMode) {
        inputMode = nextMode;
        symbolsMode = false;
        rebuildKeyboard();
        refreshCandidates();
    }

    private void handleTextKey(InputConnection ic, String key) {
        String value = labelFor(key);
        char ch = value.charAt(0);
        if (symbolsMode) {
            ic.commitText(value, 1);
            return;
        }
        if (inputMode == InputMode.ENGLISH) {
            commitComposing(ic);
            ic.commitText(String.valueOf(ch), 1);
            if (shiftOn) {
                shiftOn = false;
                rebuildKeyboard();
            }
            return;
        }
        if (engine.isComposingChar(ch)) {
            composing.append(Character.toLowerCase(ch));
            candidateOffset = 0;
            refreshComposingUi();
            refreshCandidates();
            return;
        }
        commitComposing(ic);
        ic.commitText(String.valueOf(ch), 1);
    }

    private void handleBackspace(InputConnection ic) {
        if (composing.length() > 0) {
            composing.deleteCharAt(composing.length() - 1);
            candidateOffset = 0;
            refreshComposingUi();
            refreshCandidates();
        } else {
            ic.deleteSurroundingText(1, 0);
        }
    }

    private void handleSpace(InputConnection ic) {
        if (composing.length() > 0) {
            List<String> candidates = getCandidates();
            if (!candidates.isEmpty()) {
                commitCandidate(ic, candidates.get(0));
                return;
            }
            commitComposingAsRaw(ic);
        } else {
            ic.commitText(" ", 1);
        }
    }

    private void commitComposing(InputConnection ic) {
        if (composing.length() == 0) {
            return;
        }
        List<String> candidates = getCandidates();
        if (!candidates.isEmpty()) {
            commitCandidate(ic, candidates.get(0));
        } else {
            commitComposingAsRaw(ic);
        }
    }

    private List<String> getCandidates() {
        PinyinEngine.ScriptMode scriptMode = inputMode == InputMode.TRADITIONAL
            ? PinyinEngine.ScriptMode.TRADITIONAL
            : PinyinEngine.ScriptMode.SIMPLIFIED;
        return engine.getCandidates(composing.toString(), scriptMode);
    }

    private void commitComposingAsRaw(InputConnection ic) {
        if (composing.length() == 0) {
            return;
        }
        ic.commitText(composing.toString(), 1);
        composing.setLength(0);
        candidateOffset = 0;
        refreshComposingUi();
        refreshCandidates();
    }

    private void commitCandidate(InputConnection ic, String candidate) {
        ic.commitText(candidate, 1);
        composing.setLength(0);
        candidateOffset = 0;
        refreshComposingUi();
        refreshCandidates();
    }

    private void refreshComposingUi() {
        if (composingText == null) {
            return;
        }
        if (composing.length() == 0) {
            composingText.setText(getString(R.string.composing_hint));
            composingText.setAlpha(0.65f);
        } else {
            composingText.setText(composing.toString());
            composingText.setAlpha(1f);
        }
    }

    private void refreshCandidates() {
        if (candidateContainer == null) {
            return;
        }
        candidateContainer.removeAllViews();
        currentCandidates.clear();
        currentCandidates.addAll(getCandidates());
        int end = Math.min(candidateOffset + 5, currentCandidates.size());
        for (int i = candidateOffset; i < end; i++) {
            final String candidate = currentCandidates.get(i);
            Button button = new Button(this);
            button.setText(candidate);
            button.setSingleLine(true);
            styleButton(button);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4, 0, 4, 0);
            button.setLayoutParams(lp);
            button.setOnClickListener(v -> {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    commitCandidate(ic, candidate);
                }
            });
            candidateContainer.addView(button);
        }
        boolean hasCandidates = !currentCandidates.isEmpty();
        candidatePrev.setEnabled(hasCandidates && candidateOffset > 0);
        candidateNext.setEnabled(hasCandidates && candidateOffset + 5 < currentCandidates.size());
    }

    private void pageCandidates(int delta) {
        if (currentCandidates.isEmpty()) {
            return;
        }
        int next = candidateOffset + delta;
        if (next < 0) {
            next = 0;
        }
        if (next >= currentCandidates.size()) {
            next = Math.max(0, currentCandidates.size() - 5);
        }
        if (next != candidateOffset) {
            candidateOffset = next;
            refreshCandidates();
        }
    }

    private void applyThemeColors() {
        if (rootView == null || composingText == null || candidatePrev == null || candidateNext == null) {
            return;
        }
        boolean darkMode = ImePreferences.isDarkMode(this);
        int bg = darkMode ? DARK_BG : LIGHT_BG;
        int panel = darkMode ? DARK_PANEL : LIGHT_PANEL;
        int text = darkMode ? DARK_TEXT : LIGHT_TEXT;

        rootView.setBackgroundColor(bg);
        composingText.setBackgroundColor(panel);
        composingText.setTextColor(text);
        styleButton(candidatePrev);
        styleButton(candidateNext);
        styleModeButton(modeSimplified, inputMode == InputMode.SIMPLIFIED, darkMode);
        styleModeButton(modeTraditional, inputMode == InputMode.TRADITIONAL, darkMode);
        styleModeButton(modeEnglish, inputMode == InputMode.ENGLISH, darkMode);
        styleChildren(row1);
        styleChildren(row2);
        styleChildren(row3);
        styleChildren(row4);
        styleChildren(candidateContainer);
    }

    private void styleChildren(ViewGroup parent) {
        if (parent == null) {
            return;
        }
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof Button) {
                styleButton((Button) child);
            }
        }
    }

    private void styleButton(Button button) {
        boolean darkMode = ImePreferences.isDarkMode(this);
        button.setTextColor(darkMode ? DARK_TEXT : LIGHT_TEXT);
        button.setBackgroundColor(darkMode ? DARK_PANEL : Color.WHITE);
    }

    private void styleModeButton(Button button, boolean active, boolean darkMode) {
        if (button == null) {
            return;
        }
        button.setTextColor(darkMode ? DARK_TEXT : LIGHT_TEXT);
        if (active) {
            button.setBackgroundColor(darkMode ? DARK_ACTIVE : LIGHT_ACTIVE);
        } else {
            button.setBackgroundColor(darkMode ? DARK_PANEL : Color.WHITE);
        }
    }
}
