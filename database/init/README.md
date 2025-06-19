# Database Structure

Bu dizin, Test Yönetim Sistemi'nin veritabanı yapısını oluşturan SQL dosyalarını içerir. Sistem, kullanıcıların Excel dosyalarından test senaryolarını yüklemesine ve yönetmesine olanak tanır.

## Dosya Yapısı

1. `01_create_schema.sql`: Proje ve Excel dosya yönetimi
2. `02_backlog_and_testcases.sql`: User Story ve Test Case yönetimi
3. `03_test_runs_and_results.sql`: Test çalıştırma ve sonuç yönetimi
4. `04_users_and_roles.sql`: Kullanıcı ve rol yönetimi

## Sistem Hiyerarşisi

```
Users (Kullanıcılar)
   └── Projects (Projeler)
         ├── Project Excel Files (Excel Dosyaları)
         │     └── Excel Sheets (Excel Sayfaları)
         │
         ├── Product Backlog Items (User Story'ler - US_01, US_02...)
         │     └── Acceptance Criteria (Kabul Kriterleri)
         │
         └── Test Cases (Test Senaryoları - TC01, TC02...)
               └── Test Steps (Test Adımları)
```

## Tablo İlişkileri

### 1. Kullanıcı Yönetimi

- `users`: Kullanıcı bilgileri (username, email, password...)
- `roles`: Kullanıcı rolleri (ADMIN, USER...)
- `user_roles`: Kullanıcı-rol ilişkileri

### 2. Proje Yönetimi

- `projects`: Ana proje bilgileri
- `project_excel_files`: Projeye yüklenen Excel dosyaları
- `excel_sheets`: Excel dosyalarındaki sayfalar

### 3. Test Yönetimi

- `product_backlog_items`: User Story'ler ve kabul kriterleri
- `test_cases`: Test senaryoları
- `test_steps`: Test adımları
- `test_case_excel_mapping`: Test case'lerin Excel'deki konumları

### 4. Test Sonuçları

- `test_runs`: Test koşum bilgileri
- `test_results`: Test sonuçları
- `screenshots`: Test sırasında alınan ekran görüntüleri

## Kullanım Akışı

1. **Kullanıcı Girişi**

   - Kullanıcı sisteme giriş yapar (`users` tablosu)
   - Kullanıcı yetkisi kontrol edilir (`roles` tablosu)

2. **Proje Yönetimi**

   - Kullanıcı proje oluşturur veya seçer (`projects` tablosu)
   - Excel dosyası yüklenir (`project_excel_files` tablosu)

3. **Test Case Yönetimi**

   - Excel'den User Story'ler okunur (`product_backlog_items` tablosu)
   - Test Case'ler oluşturulur (`test_cases` tablosu)
   - Test adımları kaydedilir (`test_steps` tablosu)

4. **Test Çalıştırma**
   - Test koşumu başlatılır (`test_runs` tablosu)
   - Test sonuçları kaydedilir (`test_results` tablosu)
   - Ekran görüntüleri saklanır (`screenshots` tablosu)

## Excel Formatı

Excel dosyaları iki ana sayfa içerir:

1. **Backlog Sayfası**: User Story'ler ve kabul kriterleri

   - User ID (örn: US_01)
   - Description
   - Acceptance Criteria
   - Home
   - Validation

2. **Test Cases Sayfası**: Test senaryoları ve adımları
   - US ID
   - TC ID
   - Test Objective
   - Pre-Condition
   - Steps
   - Test Data
   - Expected Result
   - Actual Result
   - Home
