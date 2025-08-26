import styles from '../../styles/components/loading.module.scss';

const Loading = ({ words = '로딩 중입니다' }) => {
  return (
    <div className={styles.loading}>
      <div className={styles.loadingText}>
        {words.split('').map((char, idx) => (
          <span key={idx}>{char}</span>
        ))}
      </div>
    </div>
  );
};
export default Loading;
