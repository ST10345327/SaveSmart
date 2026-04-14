# SaveSmart Project Gantt Chart

**Project:** SaveSmart - Personal Budget Tracker  
**Student:** Olebogeng Phawe (ST10345327)  
**Timeline:** April 11 - May 31, 2026

---

## Phase Timeline

### Phase 1: Project Setup & Database Layer
**Status:** ✅ COMPLETE  
**Duration:** April 11-13, 2026 (3 days)

```
Apr 11: |████| Project initialization
Apr 12: |████| Gradle & dependencies
Apr 13: |████████████| Database design & implementation
        └─ Room entities (5 entities)
        └─ DAOs (5 DAOs)
        └─ Repository pattern
        └─ Utility classes
```

---

### Phase 2: Authentication & UI
**Status:** ✅ COMPLETE  
**Duration:** April 13-14, 2026 (2 days)

```
Apr 13: |██████| LoginFragment implementation
Apr 14: |████████████| 
        └─ AuthViewModel
        └─ Navigation graph
        └─ Resource files
        └─ Material Design 3
        └─ Fixed 12 binding errors
        └─ CI/CD setup
```

---

### Phase 3: Categories Management (NEXT)
**Status:** ⏳ PLANNED  
**Duration:** April 15-17, 2026 (3 days)

```
Apr 15: |██████| CategoryFragment & UI design
Apr 16: |████████████| CRUD operations & validation
Apr 17: |██████| Testing & refinement
        └─ Add category screen
        └─ Edit category screen
        └─ Delete category (soft delete)
        └─ Color selection
        └─ Budget goal input
```

---

### Phase 4: Expense Tracking
**Status:** ⏳ PLANNED  
**Duration:** April 18-21, 2026 (4 days)

```
Apr 18: |████████| AddExpenseFragment design
Apr 19: |████████████| Camera & gallery integration
Apr 20: |████████████| Expense list & filtering
Apr 21: |████████| Testing & UI refinement
        └─ Expense entry form
        └─ Receipt photo capture
        └─ Expense list view
        └─ Date range filtering
        └─ Category filtering
```

---

### Phase 5: Dashboard & Reports
**Status:** ⏳ PLANNED  
**Duration:** April 22-25, 2026 (4 days)

```
Apr 22: |████████| Dashboard widgets design
Apr 23: |████████████| Progress bars & color coding
Apr 24: |████████████| Charts (pie & bar)
Apr 25: |████████| Testing & optimization
        └─ Spending progress bars
        └─ Category breakdown
        └─ Daily spending chart
        └─ Category pie chart
        └─ Budget alerts
```

---

### Phase 6: Rewards & Gamification
**Status:** ⏳ PLANNED  
**Duration:** April 26-27, 2026 (2 days)

```
Apr 26: |████████████| Points & badges system
Apr 27: |████████| Leaderboard & achievements
        └─ Points calculation
        └─ Badge generation
        └─ Leaderboard ranking
        └─ Achievement display
```

---

### Phase 7: Testing & QA
**Status:** ⏳ PLANNED  
**Duration:** April 28-29, 2026 (2 days)

```
Apr 28: |████████████| Unit tests & instrumented tests
Apr 29: |████████| Bug fixes & optimization
        └─ Minimum 80% code coverage
        └─ All screens tested
        └─ Edge cases handled
        └─ Performance optimized
```

---

### Phase 8: Documentation & Deployment
**Status:** ⏳ PLANNED  
**Duration:** April 30 - May 2, 2026 (3 days)

```
Apr 30: |████████| Complete documentation
May 01: |████████████| Generate APK & prepare submission
May 02: |████████| Final review & submission
        └─ README complete
        └─ Code comments finalized
        └─ Commit history cleaned
        └─ APK ready
        └─ Demo video prepared
```

---

## Detailed Milestones

### ✅ Completed (Phase 1)
- [x] Project initialization
- [x] Gradle configuration
- [x] Room database design
- [x] Entity-Relationship model
- [x] CRUD DAOs
- [x] Repository pattern
- [x] Login UI with MVVM
- [x] Navigation graph
- [x] Material Design 3
- [x] GitHub Actions CI/CD
- [x] Security hardening

---

### ⏳ In Progress / Upcoming

#### Week 1 (Apr 15-21)
- [ ] Categories management
- [ ] Expense tracking
- [ ] Receipt capture

#### Week 2 (Apr 22-28)
- [ ] Dashboard & reports
- [ ] Charts implementation
- [ ] Rewards system
- [ ] Unit tests

#### Week 3 (Apr 29 - May 2)
- [ ] Final testing
- [ ] Documentation
- [ ] APK generation
- [ ] Submission

---

## Resource Allocation

### Development Time Distribution

```
Database Layer:      15% ✅ Complete
Authentication:      15% ✅ Complete
UI/UX Design:        25% ⏳ In progress
Features:            30% ⏳ Planned
Testing:             10% ⏳ Planned
Documentation:        5% ✅ In progress
```

---

## Dependency Chain

```
Database Layer (DONE)
        ↓
Authentication (DONE)
        ↓
        ├─→ Categories (Apr 15-17)
        │       ↓
        ├─→ Expenses (Apr 18-21)
        │       ↓
        ├─→ Dashboard (Apr 22-25)
        │       ↓
        ├─→ Reports (Apr 22-25)
        │
        ├─→ Rewards (Apr 26-27)
        │
        ├─→ Testing (Apr 28-29)
        │
        └─→ Submission (Apr 30 - May 2)
```

---

## Risk Timeline

| Risk | Phase | Mitigation | Status |
|------|-------|-----------|--------|
| Database design changes | 1 | Early design review | ✅ Resolved |
| Navigation complexity | 2 | Navigation Component | ✅ Handled |
| UI performance | 5 | Pagination & optimization | ⏳ Planned |
| Test coverage gaps | 7 | TDD approach | ⏳ Planned |
| Submission deadline | 8 | Buffer time built in | ✅ On track |

---

## Progress Tracking

### Completion Percentage by Phase

```
Phase 1 (Setup & DB):     ████████████████████ 100% ✅
Phase 2 (Auth & UI):      ████████████████████ 100% ✅
Phase 3 (Categories):     ░░░░░░░░░░░░░░░░░░░░   0% ⏳
Phase 4 (Expenses):       ░░░░░░░░░░░░░░░░░░░░   0% ⏳
Phase 5 (Reports):        ░░░░░░░░░░░░░░░░░░░░   0% ⏳
Phase 6 (Rewards):        ░░░░░░░░░░░░░░░░░░░░   0% ⏳
Phase 7 (Testing):        ░░░░░░░░░░░░░░░░░░░░   0% ⏳
Phase 8 (Deploy):         ░░░░░░░░░░░░░░░░░░░░   0% ⏳

Overall:                  ███████░░░░░░░░░░░░░  25% ⏳
```

---

## Key Dates

| Date | Milestone | Status |
|------|-----------|--------|
| Apr 11 | Project Start | ✅ |
| Apr 13 | DB Layer Complete | ✅ |
| Apr 14 | Auth UI Complete | ✅ |
| Apr 17 | Categories Done | ⏳ Target |
| Apr 21 | Expenses Done | ⏳ Target |
| Apr 25 | Reports Done | ⏳ Target |
| Apr 27 | Rewards Done | ⏳ Target |
| Apr 29 | Testing Done | ⏳ Target |
| May 02 | Ready for Submission | ⏳ Target |
| May 15 | POE Submission Deadline | 📅 |

---

## Buffer & Contingency

- **Buffer Time:** 2 weeks (Apr 29 - May 15)
- **Contingency:** 5 days for unexpected issues
- **Review Time:** 5 days for final refinement

---

## Success Criteria

- [x] Phase 1 Complete (Database & Auth)
- [ ] Phase 2-6 Complete (Features)
- [ ] 80%+ test coverage
- [ ] All requirements traced (R01-R23, T01-T10)
- [ ] No compilation errors
- [ ] Professional documentation
- [ ] Clean Git history
- [ ] Working APK generated

---

**Status:** ✅ ON SCHEDULE  
**Last Updated:** April 14, 2026  
**Next Review:** April 17, 2026

