        package com.realestate.gestion_immobiliere.model;

        import org.springframework.format.annotation.DateTimeFormat;

        import jakarta.persistence.*;
        import java.time.temporal.ChronoUnit;
        import java.time.LocalDate;
        @Entity
        @Table(name = "transactions")
        public class Transaction {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

                @ManyToOne
                @JoinColumn(name = "bien_id")
                private Bien bien;

            @ManyToOne
            @JoinColumn(name = "user_id")
            private User user;

            @Column(name = "start_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            private LocalDate startDate;

            @Column(name = "end_date")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            private LocalDate endDate;

            // Constructors
            public Transaction() {
            }

            // Getters and Setters
            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public Bien getBien() {
                return bien;
            }

            public void setBien(Bien bien) {
                this.bien = bien;
            }

            public User getUser() {
                return user;
            }

            public void setUser(User user) {
                this.user = user;
            }

            public LocalDate getStartDate() {
                return startDate;
            }

            public void setStartDate(LocalDate startDate) {
                this.startDate = startDate;
            }

            public LocalDate getEndDate() {
                return endDate;
            }

            public void setEndDate(LocalDate endDate) {
                this.endDate = endDate;
            }
            public double getAmount() {
                if (bien == null || startDate == null || endDate == null) {
                    return 0;
                }
                long months = ChronoUnit.MONTHS.between(startDate, endDate);
                return bien.getPrix() * months;
            }
        }
