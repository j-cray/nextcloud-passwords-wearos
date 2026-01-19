# Nextcloud Passwords WearOS

<div align="center">

**Your Credentials, On Hand**

[![WearOS](https://img.shields.io/badge/Platform-WearOS-blue?style=for-the-badge&logo=wearos)]()
[![Nextcloud](https://img.shields.io/badge/Ecosystem-Nextcloud-blue?style=for-the-badge&logo=nextcloud)]()

*A wearable client for the Nextcloud Passwords app.*

</div>

---

## 📖 Overview
This app brings your Nextcloud Passwords database to your wrist. It is designed for quick access to 2FA codes, PINs, or short passwords when pulling out your phone is inconvenient.

**Note:** This is a client for the [Nextcloud Passwords](https://apps.nextcloud.com/apps/passwords) app, not the default encryption module.

---

## 🗺️ Roadmap & Todo

### Phase 1: Security & Auth
- [ ] **Login:** Implement the Nextcloud Login Flow (via QR code from phone app).
- [ ] **Encryption:** Ensure local cache is encrypted on the watch.
- [ ] **Biometrics:** Require Pattern or PIN to open the app (if watch is unlocked).

### Phase 2: Core Features
- [ ] **List View:** Browse folders and password entries.
- [ ] **Search:** Voice or keyboard search for entries.
- [ ] **Details:** View username, password (hidden by default), and notes.

### Phase 3: The "Killer Feature"
- [ ] **TOTP:** Display rotating 2FA codes directly on the watch face or in-app (if supported by backend).

### Phase 4: UX Polish
- [ ] **Complication:** Add a shortcut complication to the watch face.
- [ ] **Tile:** Create a "Favorites" tile for your most-used credentials.
