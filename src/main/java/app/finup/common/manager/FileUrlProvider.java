package app.finup.common.manager;

/**
 * 현재 프로젝트 프로필 상태에 따라 달라지는 파일 주소 제공
 * @author kcw
 * @since 2025-11-29
 */
public interface FileUrlProvider {

    String getFullPath(String path);
}
