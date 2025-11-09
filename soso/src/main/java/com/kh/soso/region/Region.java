package com.kh.soso.region;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter
@Table(name= "region")
public class Region {

	 @Id
	    @Column(name = "region_no")
	    private Long regionNo; // (예: 1111010100 - 10자리)

	    @Column(name = "region_name", nullable = false, length = 100)
	    private String regionName;

	    @Column(name = "region_depth1", length = 30)
	    private String regionDepth1;

	    @Column(name = "region_depth2", length = 30)
	    private String regionDepth2;

	    @Column(name = "region_depth3", length = 30)
	    private String regionDepth3;

	    @Column(name = "x_coord")
	    private Double xCoord; // 경도 (Longitude)

	    @Column(name = "y_coord")
	    private Double yCoord; // 위도 (Latitude)
}
