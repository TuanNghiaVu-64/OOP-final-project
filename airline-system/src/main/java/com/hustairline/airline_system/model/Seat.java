package com.hustairline.airline_system.model;

public class Seat extends BaseEntity {
    private int id;
    private int planeId;
    private String seatCode;
    private int row;
    private int col;
    private Integer seatTypeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaneId() {
        return planeId;
    }

    public void setPlaneId(int planeId) {
        this.planeId = planeId;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Integer getSeatTypeId() {
        return seatTypeId;
    }

    public void setSeatTypeId(Integer seatTypeId) {
        this.seatTypeId = seatTypeId;
    }

    @Override
    public boolean isValid() {
        return planeId > 0 && seatCode != null && !seatCode.trim().isEmpty();
    }

    @Override
    public String getEntityType() {
        return "Seat";
    }
} 