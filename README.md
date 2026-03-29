# Convoy Pinyin IME

Minimal offline Android keyboard inspired by Google Pinyin Input.

## Current scope
- qwerty on-screen keyboard
- Simplified / Traditional Chinese / English mode cycle
- offline pinyin candidate strip
- CC-CEDICT-derived offline lexicon asset
- punctuation/symbol toggle
- backspace, space, enter, shift
- dark mode toggle
- launcher activity for enable/select flow

## Not included
- cloud sync
- online prediction
- handwriting
- voice input
- gesture typing
- full phrase model

## Build
```bash
./gradlew :app:assembleDebug
```

## Install and enable
1. Install the APK.
2. Open `Convoy Pinyin`.
3. Tap `Open input settings` and enable the IME.
4. Tap `Open input picker` and switch to Convoy Pinyin.

## Dictionary data
- Candidates are loaded from `app/src/main/assets/pinyin_lexicon.tsv`.
- The asset is generated from CC-CEDICT with `tools/build_lexicon.py`.
- Simplified and Traditional candidates are stored side by side for offline lookup.
