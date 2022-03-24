package invaded.cc.core.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.image.BufferedImage;
import java.util.List;

@RequiredArgsConstructor
@Data
public class Skin {

    public static final Skin STEVE_SKIN = new Skin("eyJ0aW1lc3RhbXAiOjE1MjY4MDczNDc1MjgsInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2N2Q0ODMyNWVhNTMyNDU2MTQwNmI4YzgyYWJiZDRlMjc1NWYxMTE1M2NkODVhYjA1NDVjYzIifX19",
            "fzBEi4s/C8nj18tVHIWBlWQEObm65IfATc4P83iB3PEHE44M0wGQjpyT+ZQGXcbgNeVFQ0FPyEwA8/SC3iGrZ5eKy8s5LCfI5LvOPWYQLHPlwEmRgix9dhpf+vKVrirUEkzDuY/oM3cRfLBbNk1afl+df36oixeG4cqsbLEnSJRu/kOMtA5Fcic2NQf7g402pNeqD2D8cq4Hbe47g2UcfIRVsGt0fLif2qsojbha5m6dYYUEfJOmNcGqPiubznxgGS3vpQ8GHRZrntMJbmywrDAOZjgxNmi+Bdq476nJ84NZycBe3BqgtmKFp+WF6z6jxPeQ1ZcUnlEzmsRJwhfS7zHb4Ujyvzn5BxzMegTmsP33cplCydcd/2oXhKnMj4xtmQtrHS10aUs4oa2M7Ak60SVm11qAOR1KwGvMcDY37shvzjK/4cwuspfsgSBIlVC6MJGBgqmc571LWixSJYBRl2HvW/ao43XbN8k9/oegh7SBJMusdO3ADtbOmt84GmzoEbLfWTi4uEkJpYkPfK4UiqvTnB0Uw+KyRJCdoRwpDNRVMZFTb/eJO4Cr2tAIVTM1JR1E5hWaQ7IQBH+Bwj39JjBpK7MLpx0jjZV+y09+u3BrUIVgrLYFQP0WZxypw45+SAuk/P35hG10ERGjwYRZ6PMWnevq13fYUHlc0Crbn3I=");
    private final String texture, signature;
    private String head = "";
    private BufferedImage image;
    private List<String> loreHead;

    public String toString() {
        return texture + ":" + signature;
    }

}
