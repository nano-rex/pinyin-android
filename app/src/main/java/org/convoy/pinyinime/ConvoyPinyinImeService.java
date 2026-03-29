package org.convoy.pinyinime;

import android.inputmethodservice.InputMethodService;
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
    private enum InputMode {
        SIMPLIFIED,
        TRADITIONAL,
        ENGLISH
    }

    private static final String[] ROW1 = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"};
    private static final String[] ROW2 = {"a", "s", "d", "f", "g", "h", "j", "k", "l"};
    private static final String[] ROW3 = {"Shift", "z", "x", "c", "v", "b", "n", "m", "⌫"};
    private static final String[] ROW4 = {"中", "符", ",", "Space", ".", "Enter"};
    private static final String[] SYMBOLS = {"，", "。", "？", "！", "：", "；", "（", "）", "“", "”", "、", "-"};

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
    private LinearLayout row1;
    private LinearLayout row2;
    private LinearLayout row3;
    private LinearLayout row4;

    @Override
    public View onCreateInputView() {
        View root = getLayoutInflater().inflate(R.layout.input_view, null);
        composingText = root.findViewById(R.id.composing_text);
        candidateContainer = root.findViewById(R.id.candidate_container);
        candidatePrev = root.findViewById(R.id.candidate_prev);
        candidateNext = root.findViewById(R.id.candidate_next);
        row1 = root.findViewById(R.id.row1);
        row2 = root.findViewById(R.id.row2);
        row3 = root.findViewById(R.id.row3);
        row4 = root.findViewById(R.id.row4);

        candidatePrev.setOnClickListener(v -> pageCandidates(-5));
        candidateNext.setOnClickListener(v -> pageCandidates(5));

        rebuildKeyboard();
        refreshComposingUi();
        refreshCandidates();
        return root;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        composing.setLength(0);
        candidateOffset = 0;
        refreshComposingUi();
        refreshCandidates();
    }

    private void rebuildKeyboard() {
        row1.removeAllViews();
        row2.removeAllViews();
        row3.removeAllViews();
        row4.removeAllViews();
        addRow(row1, ROW1);
        addRow(row2, ROW2);
        addRow(row3, ROW3);
        addRow(row4, symbolsMode ? SYMBOLS : ROW4);
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
        if ("Shift".equals(key) || "⌫".equals(key) || "中".equals(key) || "En".equals(key) || "符".equals(key) || "Space".equals(key) || "Enter".equals(key)) {
            return 1.5f;
        }
        return 1f;
    }

    private String labelFor(String key) {
        if ("中".equals(key) || "En".equals(key)) {
            switch (inputMode) {
                case TRADITIONAL:
                    return getString(R.string.mode_tw);
                case ENGLISH:
                    return getString(R.string.mode_en);
                case SIMPLIFIED:
                default:
                    return getString(R.string.mode_cn);
            }
        }
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
            case "中":
            case "En":
                cycleInputMode();
                symbolsMode = false;
                rebuildKeyboard();
                refreshCandidates();
                return;
            case "符":
                symbolsMode = !symbolsMode;
                rebuildKeyboard();
                return;
            default:
                handleTextKey(ic, key);
        }
    }

    private void cycleInputMode() {
        switch (inputMode) {
            case SIMPLIFIED:
                inputMode = InputMode.TRADITIONAL;
                break;
            case TRADITIONAL:
                inputMode = InputMode.ENGLISH;
                break;
            case ENGLISH:
            default:
                inputMode = InputMode.SIMPLIFIED;
                break;
        }
    }

    private void handleTextKey(InputConnection ic, String key) {
        String value = labelFor(key);
        char ch = value.charAt(0);
        if (symbolsMode) {
            commitComposing(ic);
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
}
